---

# Apollo

A data analysis and prediction project using open-source data and machine learning to identify patterns in global terrorism and predict terrorist incidents. The project includes a mobile application built for Android to facilitate these predictions, featuring real-time data collection, model training, and visualization of predictive probabilities.


## Background

Terrorism, defined as "the threatened or actual use of illegal force and violence by a non-state actor to attain a political, economic, religious, or social goal through fear, coercion, or intimidation," is a persistent global threat. This project, "Apollo," aims to leverage machine learning and data analysis to predict the likelihood of terrorist incidents occurring over time, providing a valuable tool for policy makers and security professionals. 

The Android app "Apollo" allows users to train predictive models, input features for predictions, and visualize the results in a user-friendly interface.


## Objectives

- To develop an Android application that allows users to:
  - Train a machine learning model using historical terrorism data.
  - Input features to predict the probability of terrorist incidents.
  - Visualize prediction probabilities over time.
  - Display model performance metrics such as accuracy and F1 scores.
  
- To build an interactive and user-friendly interface that provides clear insights and actionable information.



## UI

![alt text](https://github.com/idee24/apollo_android/blob/master/figma_display.png?raw=true)



## Data

The data used in this project is sourced from the Global Terrorism Database (GTD) available on Kaggle. The dataset consists of 181,691 rows and 135 columns, including variables such as `country_txt` (name of the country), `nkill` (number of individuals killed), and `iyear` (year of attack). The data can be accessed at: [Global Terrorism Database on Kaggle](https://www.kaggle.com/datasets/START-UMD/gtd).

**Data Requirements:**
- Open-source and from an authoritative source.
- Includes non-anonymized column names.
- Ideally recent (within the last 10 years).
- Contains at least 2-3 continuous variables and 2-3 categorical variables.
- Includes at least 1,500 rows.
- Contains geographical information for geospatial analysis.
- Includes time series data for temporal analysis.

Due to the large file size, not all data files are included in the repository. Additional data can be accessed here: [Additional Data Files](https://drive.google.com/drive/folders/1YBUpamuBOEgm0tncopqjzuR3YreouSuT?usp=sharing).



## Tools and Technologies

**Language:** Python, Kotlin, Java  
**Libraries:** Chaquopy, NumPy, pandas, matplotlib, scikit-learn, datetime, openai, jetpack compose  
**Tools:** Jupyter notebooks, Android Studio, GitHub, Figma, DALL-E for mockups

**Skills Demonstrated:**

- **Data Wrangling and Cleaning:** Dropped unnecessary columns, renamed columns, addressed mixed or incorrect data types, handled missing values, and removed duplicates.
- **Exploratory Data Analysis:** Explored descriptive statistics, visualized data distributions using scatterplots, correlation heatmaps, and categorical plots.
- **Modeling:** Implemented a Random Forest classifier for prediction, conducted feature importance analysis, and evaluated model performance using accuracy and F1 scores.
- **Time-Series Analysis:** Generated and visualized predicted probabilities over time, and used machine learning models to analyze temporal trends.
- **UI/UX Design:** Designed a user-friendly interface for the Android app, including data collection forms, visualization screens, and real-time prediction updates.
- **Reporting:** Provided explanations and summaries of model predictions using GPT-3.5 to ensure the results are understandable to non-experts.



## Application Features

1. **Splash Screen:** Displays the Apollo logo and app name, with an option to initiate the model training phase. The screen includes a progress bar to show training progress, and options to delete the model and training data.
   
2. **Prediction Feature Collection Screen:** Allows users to input key features such as the year, month, day, number of kills, number of wounded, country, region, attack type, target type, and weapon type.

3. **Results Visualization Screen:** Shows a graph of predicted probabilities over time, provides an explanation of the time series trends, and displays a table with model accuracy and F1 scores.

## How to Use

1. **Install the Apollo App:** Download and install the APK on your Android device.
2. **Initiate Model Training:** Launch the app and initiate the model training on the splash screen. Wait until the training is complete.
3. **Input Prediction Features:** Navigate to the feature collection screen and input the relevant data points.
4. **View Predictions:** After entering the data, view the prediction results on the results screen. Analyze the time series graph and review the model's performance metrics.



## Future Work

- Integrate more advanced machine learning models to improve prediction accuracy.
- Expand the dataset to include more recent and comprehensive terrorism data.
- Implement additional features such as real-time data updates and notifications.
- Optimize the app for performance and usability across a wider range of devices(react_native).



## Contributions

This project was developed as a collaborative effort. Contributions, suggestions, and improvements are welcome. Please submit pull requests or open issues on the GitHub repository.



## License

This project is licensed under the MIT License. See the LICENSE file for details.

---
