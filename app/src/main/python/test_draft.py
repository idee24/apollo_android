import pandas as pd
from openai import OpenAI
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import OneHotEncoder
from sklearn.pipeline import Pipeline
import pickle
import json
from typing import Dict


def initApollo(filePath, start_date, end_date, casualties, targetType, weaponsType, attackType, country):
    # Load and preprocess the data
    gtd = pd.read_csv(filePath, encoding='ISO-8859-1', low_memory=False)
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
    print(f'Accuracy: {accuracy_score(y_test, y_pred)}')
    print(f'Classification Report:\n{classification_report(y_test, y_pred)}')


    features =  {
        "iyear": 0,
        "imonth": 0,
        "iday": 0,
        "nkill": casualties,
        "nwound": casualties,
        "country_txt": 0,
        "region_txt_Middle East & North Africa": 1,
        "region_txt": 0,
        "attacktype1_txt_Assassination": 1,
        "attacktype1_txt_Bombing/Explosion": 0,
        "targtype1_txt_Military": 1,
        "targtype1_txt":targetType,
        "targtype1_txt_Civilians": 0,
        "weaptype1_txt_Explosives": 1,
        "weaptype1_txt": weaponsType,
        "attacktype1_txt_Armed": 0,
        "attacktype1_txt_Facility/Infrastructure Attack":1,
        "attacktype1_txt":attackType,
        "attacktype1_txt_Hostage (Barricade Incident)":0,
        "":1
    }


    required_columns = ['iyear', 'imonth', 'iday', 'country_txt', 'region_txt', 'attacktype1_txt', 'targtype1_txt', 'weaptype1_txt', 'nkill', 'nwound']
    for col in required_columns:
        if col not in features:
            print("Missing col => not in feature ===> " + col)

    # Convert input to DataFrame and ensure correct data types
    features_df = pd.DataFrame([features])

    # Convert types
    numeric_columns = ['iyear', 'imonth', 'iday', 'nkill', 'nwound']
    for col in numeric_columns:
        features_df[col] = pd.to_numeric(features_df[col], errors='coerce')

    categorical_columns = ['country_txt', 'region_txt', 'attacktype1_txt', 'targtype1_txt', 'weaptype1_txt']
    for col in categorical_columns:
        features_df[col] = features_df[col].astype(str)

    # Check for NaN values after type conversion
    if features_df.isnull().values.any():
        print("Data frame has NAN values")

    # Use the pipeline to ensure

    prediction = pipeline.predict(features_df)
    prediction_proba = pipeline.predict_proba(features_df)
    feature_importances = pipeline.named_steps['classifier'].feature_importances_


    print('predictions ==> ' + str(int(prediction[0])))
    print('prediction_probability ==> ' + str(prediction_proba[0].tolist()))
    print('feature_importances ==> ' + str(feature_importances.tolist()))

    client = OpenAI(api_key="")

    def get_time_series_from_dataframe(df: pd.DataFrame) -> Dict[str, float]:
        time_series = {}
        for _, row in df.iterrows():
            date = row['Date'].strftime('%Y-%m-%d')
            probability = row['Probability']
            time_series[date] = probability
        return time_series



    def generate_time_series_predictions(pipeline, features, start_date, end_date):
        """
        Generate a time series of predictions by varying the date.

        :param pipeline: The trained machine learning pipeline.
        :param features: A dictionary of the features with initial values.
        :param start_date: The start date for the time series (YYYY-MM-DD).
        :param end_date: The end date for the time series (YYYY-MM-DD).
        :return: A DataFrame with dates and corresponding prediction probabilities.
        """
        date_range = pd.date_range(start=start_date, end=end_date)
        probabilities = []
        dates = []

        for single_date in date_range:
            features['iyear'] = single_date.year
            features['imonth'] = single_date.month
            features['iday'] = single_date.day

            # Convert input to DataFrame and ensure correct data types
            features_df = pd.DataFrame([features])

            # Convert types
            numeric_columns = ['iyear', 'imonth', 'iday', 'nkill', 'nwound']
            for col in numeric_columns:
                features_df[col] = pd.to_numeric(features_df[col], errors='coerce')

            categorical_columns = ['country_txt', 'region_txt', 'attacktype1_txt', 'targtype1_txt', 'weaptype1_txt']
            for col in categorical_columns:
                features_df[col] = features_df[col].astype(str)

            # Check for NaN values after type conversion
            if features_df.isnull().values.any():
                continue  # Skip dates with invalid data

            prediction_proba = pipeline.predict_proba(features_df)[0]
            probabilities.append(prediction_proba[1])  # Probability of success
            dates.append(single_date)

        return pd.DataFrame({'Date': dates, 'Probability': probabilities})

    time_series_df = generate_time_series_predictions(pipeline, features, start_date, end_date)
    print(time_series_df)


    def analyze_time_series(time_series_df):
        """
        Analyze the time series data to identify key trends and patterns.

        :param time_series_df: DataFrame with dates and corresponding prediction probabilities.
        :return: A summary of the analysis.
        """
    summary = ""
    max_probability = time_series_df['Probability'].max()
    min_probability = time_series_df['Probability'].min()
    avg_probability = time_series_df['Probability'].mean()
    std_probability = time_series_df['Probability'].std()

    summary += f"The maximum predicted probability of a terrorist incident is {max_probability:.2f}.\n"
    summary += f"The minimum predicted probability of a terrorist incident is {min_probability:.2f}.\n"
    summary += f"The average predicted probability of a terrorist incident over the time period is {avg_probability:.2f}.\n"
    summary += f"The standard deviation of the predicted probabilities is {std_probability:.2f}, indicating the variability of the predictions.\n"

    # Identify periods of high and low probabilities
    high_prob_periods = time_series_df[time_series_df['Probability'] > avg_probability + std_probability]
    low_prob_periods = time_series_df[time_series_df['Probability'] < avg_probability - std_probability]

    if not high_prob_periods.empty:
        summary += "Periods of significantly high probabilities:\n"
        for index, row in high_prob_periods.iterrows():
            summary += f" - Date: {row['Date'].strftime('%Y-%m-%d')} with a probability of {row['Probability']:.2f}\n"
    else:
        summary += "No periods of significantly high probabilities identified.\n"

    if not low_prob_periods.empty:
        summary += "Periods of significantly low probabilities:\n"
        for index, row in low_prob_periods.iterrows():
            summary += f" - Date: {row['Date'].strftime('%Y-%m-%d')} with a probability of {row['Probability']:.2f}\n"
    else:
        summary += "No periods of significantly low probabilities identified.\n"

        return summary

    def summarize_time_series_with_llm(analysis_summary):
        """
        Use an LLM to generate a natural language summary of the time series analysis.

        :param analysis_summary: A string containing the analysis summary.
        :return: A refined natural language summary.
        """

        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "You are an expert in explaining data trends and patterns to non-experts."},
                {"role": "user", "content": f"Summarize the following analysis of predicted probabilities for terrorist incidents in simple terms for policy makers:\n\n{analysis_summary}\n\nRefined Explanation:"}
            ],
            max_tokens=150,
            temperature=0.7
        )

        return response.choices[0].message.content


    # Analyze the time series
    analysis_summary = analyze_time_series(time_series_df)
    print("Analysis Summary:\n", analysis_summary)

    # Generate a refined explanation using the LLM
    refined_summary = summarize_time_series_with_llm(analysis_summary)
    print("Refined Summary:\n", refined_summary)

    prediction_result = {
        "code": 200,
        "message": "Prediction successful",
        "prediction": {
            "featureImportance": f'Classification Report:\n{classification_report(y_test, y_pred)}',
            "timeSeries": get_time_series_from_dataframe(time_series_df),
            "analysisSummary": analysis_summary,
            "refinedSummary": refined_summary
        }
    }
    return json.dumps(prediction_result, indent=4)


