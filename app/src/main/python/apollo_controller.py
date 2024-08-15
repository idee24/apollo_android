import pandas as pd
from openai import OpenAI
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import OneHotEncoder
from sklearn.pipeline import Pipeline
import pickle

def generate_report(filepath):
    gtd = pd.read_csv(filepath, encoding='ISO-8859-1', low_memory=False)
    columns = ['iyear', 'imonth', 'iday', 'country_txt', 'region_txt', 'attacktype1_txt', 'targtype1_txt', 'weaptype1_txt', 'nkill', 'nwound', 'success']
    data = gtd[columns].copy()
    data.fillna(0, inplace=True)


    # Define features and target
    X = data.drop('success', axis=1)
    y = data['success']

    # Column Transformer to handle categorical features
    categorical_features = ['country_txt', 'region_txt', 'attacktype1_txt', 'targtype1_txt', 'weaptype1_txt']
    categorical_transformer = OneHotEncoder(handle_unknown='ignore')

    preprocessor = ColumnTransformer(
        transformers=[
            ('cat', categorical_transformer, categorical_features)
        ],
        remainder='passthrough'  # To keep the other columns ('iyear', 'imonth', 'iday', 'nkill', 'nwound')
    )

    # Create a pipeline with the preprocessor and the model
    pipeline = Pipeline(steps=[
        ('preprocessor', preprocessor),
        ('classifier', RandomForestClassifier(n_estimators=100, random_state=42))
    ])

    # Split the data
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Train the pipeline
    pipeline.fit(X_train, y_train)

    # Evaluate the model
    y_pred = pipeline.predict(X_test)
    # print(f'Accuracy: {accuracy_score(y_test, y_pred)}')
    return f'Classification Report:\n{classification_report(y_test, y_pred)}'


def get_number_of_deaths(GTD):
    """
    Calculates the percentage of incidents in the Global Terrorism Database (GTD) that resulted in at least one death.

    Parameters:
    GTD (pandas.DataFrame): A DataFrame containing terrorism-related data. It must include a column named 'nkill' which
                            represents the number of fatalities for each incident.

    Notes:
    - Ensure that the GTD DataFrame is properly preprocessed with relevant columns and without missing values for accurate results.
    """
    return "{}% of incidents occured at least one death.".format(round(float(GTD[GTD.nkill > 0]['nkill'].count())/GTD.nkill.notna().sum()*100,2))
