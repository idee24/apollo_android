
import pandas as pd
from typing import Dict



def get_time_series_from_dataframe(df: pd.DataFrame) -> Dict[str, float]:
    time_series = {}
    for _, row in df.iterrows():
        date = row['Date'].strftime('%Y-%m-%d')
        probability = row['Probability']
        time_series[date] = probability
    return time_series



def generate_time_series_predictions(gtd, model, start_date, end_date, casualties, targetType, weaponsType, attackType,
                                     country, latitude, longitude):
    """
    Generate a time series of predictions by varying the date.

    :param gtd: PD DataFrame.
    :param model: The trained machine learning pipeline.
    :param features: A dictionary of the features with initial values.
    :param start_date: The start date for the time series (YYYY-MM-DD).
    :param end_date: The end date for the time series (YYYY-MM-DD).
    :return: A DataFrame with dates and corresponding prediction probabilities.
    """
    date_range = pd.date_range(start=start_date, end=end_date, freq='M')
    probabilities = []
    dates = []

    featureList = gtd.drop(['death'], axis=1).columns
    features = {}

    for feat in featureList:
        features[feat] = 0

    for single_date in date_range:
        features['iyear'] = single_date.year
        features['imonth'] = single_date.month
        features['success'] = 1
        features['natlty1'] = country
        features['country'] = country
        features['weapsubtype1'] = weaponsType
        features['targtype1'] = targetType
        features['nwound'] = casualties
        features['nkill'] = casualties
        features['INT_MISC'] = attackType
        features['INT_IDEO'] = attackType
        features['INT_LOG'] = attackType
        features['latitude'] = latitude
        features['longitude'] = longitude

        # Convert input to DataFrame and ensure correct data types
        features_df = pd.DataFrame([features])

        # Convert types
        numeric_columns = ['iyear', 'imonth', 'nkill', 'nwound']
        for col in numeric_columns:
            features_df[col] = pd.to_numeric(features_df[col], errors='coerce')

        categorical_columns = ['country', 'INT_IDEO', 'INT_MISC','INT_LOG', 'targtype1', 'weapsubtype1']
        for col in categorical_columns:
            features_df[col] = features_df[col].astype(str)

        # Check for NaN values after type conversion
        if features_df.isnull().values.any():
            continue  # Skip dates with invalid data

        prediction_proba = model.predict_proba(features_df)[0]
        probabilities.append(prediction_proba[1])  # Probability of success
        dates.append(single_date)

    return pd.DataFrame({'Date': dates, 'Probability': probabilities})