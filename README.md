Overview
This project analyzes ad impressions and clicks, generating metrics and recommendations based on user interactions.

Branches
main: This branch contains the implementation where the processing of data is done entirely in Java without using a database.

process_with_db: This branch utilizes a MySQL database to store impressions and clicks, processing the data and generating metrics and recommendations from there.

Running the Project
Prerequisites
Java 11
Maven (for building the project)
MySQL server

Set Up the Database: Execute the ad_metrics_db.sql SQL dump to create the necessary tables in your MySQL database

Output
The results will be saved in the src/main/resources directory as:

metrics_output.json
recommendations.json
