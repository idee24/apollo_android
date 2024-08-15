from openai import OpenAI

client = OpenAI(api_key="")
def analyze_time_series(time_series_df):
    """
    Analyze the time series data to identify key trends and patterns.

    :param time_series_df: DataFrame with dates and corresponding prediction probabilities.
    :return: A summary of the analysis for further processing.
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

def summarize_time_series_with_llm(analysis_summary, country, latitude, longitude):
    """
    Use an LLM to generate a natural language summary of the time series analysis.

    :param analysis_summary: A string containing the analysis summary.
    :return: A refined natural language summary.
    """

    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "You are an expert using your knowledge of machine learning in explaining data trends and patterns to non-experts."},
                {"role": "user", "content": f"Location: {country}({latitude},{longitude}) \nIn at least 500words, provide a comprehensive explanation the following analysis of predicted probabilities for terrorist incidents in simple terms for policy makers. Easy on the figures and focus on those periods and explain the scenarios carefully (for example, between may and october 2020 high terrorist activity is predicted by the model). :\n\n{analysis_summary}\n\nRefined Explanation:"}
            ],
            temperature=0.7
        )

        return response.choices[0].message.content
    except:
         return analysis_summary