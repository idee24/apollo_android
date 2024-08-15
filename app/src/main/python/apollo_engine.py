import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report
import time
import json
import numpy as np
from scipy.stats import skew
from sklearn import preprocessing
from time_series_engine import get_time_series_from_dataframe
from time_series_engine import generate_time_series_predictions
from llm_controller import summarize_time_series_with_llm
from llm_controller import analyze_time_series
from apollo_controller import get_number_of_deaths
from sklearn.neighbors import KNeighborsClassifier
from sklearn.ensemble import RandomForestClassifier, AdaBoostClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.feature_selection import SelectFromModel
from sklearn.neural_network import MLPClassifier
from apollo_controller import generate_report




def initApollo(filePath, start_date, end_date, casualties, targetType, weaponsType, attackType,
               country, latitude, longitude):


    # Load and preprocess the data
    GTD = pd.read_csv(filePath, engine='python', on_bad_lines='skip')

    key_data = GTD[GTD['country_txt'] == country]
    country_key = (key_data['country'].unique())[0]

    # Creating the target variable
    GTD = GTD[GTD['nkill'].notnull()]
    GTD['death'] = np.where(GTD.nkill > 0, 1, 0)
    # GTD = GTD.drop(['nkill'], axis = 1)


    columns = GTD.columns
    percent_missing = GTD.isnull().sum() * 100 / len(GTD)
    unique = GTD.nunique()
    dtypes = GTD.dtypes
    missing_value_data = pd.DataFrame({'column_name': columns,
                                       'percent_missing': percent_missing,
                                       'unique': unique,
                                       'types': dtypes})
    missing_value_data = missing_value_data[missing_value_data['percent_missing']>0]
    missing_value_data=missing_value_data.sort_values(by=['percent_missing'], ascending=False)

    def missing_values(data,mis_min):
        columns = data.columns
        percent_missing = data.isnull().sum() * 100 / len(data)
        unique = data.nunique()
        missing_value_data = pd.DataFrame({'column_name': columns,
                                           'percent_missing': percent_missing,
                                           'unique': unique})
        missing_drop = list(missing_value_data[missing_value_data.percent_missing>mis_min].column_name)
        return(missing_drop)

    GTD['natlty1'].fillna(GTD['country'], inplace = True)
    missing_drop = missing_values(GTD,50)
    GTD = GTD.drop(missing_drop, axis=1)
    GTD = GTD.drop(columns = ['nkillter'])

    mode_fill = ['nwound','longitude','latitude','weapsubtype1','weapsubtype1_txt','targsubtype1',
                 'targsubtype1_txt','natlty1_txt','guncertain1','ishostkid', 'specificity',
                 'doubtterr','multiple', 'target1', 'city', 'provstate']
    for col in mode_fill:
        GTD[col].fillna(GTD[col].mode()[0], inplace=True)

    GTD['nperps'].fillna(GTD['nperps'].mean(), inplace=True)
    GTD['nperpcap'].fillna(GTD['nperpcap'].mean(), inplace=True)
    GTD['nwoundte'].fillna(GTD['nwoundte'].mean(), inplace=True)
    GTD['claimed'].fillna(0, inplace=True)
    GTD['nwoundus'].fillna(GTD['nwoundus'].mean(), inplace=True)
    GTD['nkillus'].fillna(GTD['nkillus'].mean(), inplace=True)

    GTD['suicide'] = GTD.suicide.astype('object')
    num_features = ['nperps','nkillus','nwound','nwoundus', 'nwoundte']

    # Identify skewed features
    skewed_feat = GTD[num_features].apply(lambda x: skew(x.dropna()))  # Ignore NaNs when calculating skewness
    skewed_feat = skewed_feat[skewed_feat > 0.75]
    skewed_feat = skewed_feat.index

    # Apply log1p transformation, ensuring non-negative values
    for feat in skewed_feat:
        GTD[feat] = GTD[feat].apply(lambda x: np.log1p(x) if x >= 0 else np.nan)

    duplicated_columns = [col for col in GTD.columns if "_txt" in col]
    GTD = GTD.drop(duplicated_columns, axis=1)
    cat_features = GTD.dtypes[GTD.dtypes == 'object'].index

    le = preprocessing.LabelEncoder()
    for col in cat_features:
        GTD[col] = le.fit_transform(GTD[col])
    GTD[GTD == np.inf] = np.nan
    GTD.fillna(GTD.mean(), inplace = True)


    income = GTD['death']
    # Split the 'features' and 'income' data into training and testing sets
    X_train, X_test, y_train, y_test = train_test_split(GTD.drop(['death'], axis=1),
                                                        income,
                                                        shuffle=True,
                                                        test_size = 0.2,
                                                        random_state = 43)

    clf_A = KNeighborsClassifier()
    clf_B = AdaBoostClassifier(random_state = 41)
    clf_C = RandomForestClassifier(random_state = 41)
    clf_D = GaussianNB()

    report = generate_report(filePath)

    # report = f'Classification Report:\n{accuracy_score(X_train, y_train)}'
    # Collect results on the learners
    prediction_results = {}
    analysis = []


    for clf in [clf_A, clf_B, clf_C, clf_D]:

        clf_name = clf.__class__.__name__

        sfm = SelectFromModel(clf, threshold=0.02)
        sfm.fit(X_train, y_train)
        learnr = clf.fit(X_train, y_train)


        time_series_df = generate_time_series_predictions(GTD, learnr, start_date, end_date,
                                                          casualties, targetType, weaponsType,
                                                          attackType, country_key, latitude, longitude)

        # Analyze the time series
        analysis_summary = ("Prediction Model: " + clf_name + "\n" +
                            analyze_time_series(time_series_df))

        analysis.append(analysis_summary)

        prediction = {
            "featureImportance": report,
            "timeSeries": get_time_series_from_dataframe(time_series_df),
            "analysisSummary": analysis_summary,
        }
        prediction_results[clf_name] = prediction



    # Generate a refined explanation using the LLM
    refined_summary = summarize_time_series_with_llm(analysis, country, latitude, longitude)
    res = {
        "results" : prediction_results,
        "refinedSummary": refined_summary,
        "code": 200,
        "message": "Prediction successful",
    }

    return json.dumps(res, indent=4)


