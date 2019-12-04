# SalesForce_LogisticRegression
Propensity Modeling / Logistical Regression Example to Forecast Client Engagement Probabilities

Salesforce allows you to add field to assist sales associates with their business development efforts. 

Formula fields can be used to compute a probability that a particular client will do something and allow a simple report to be used to direct your employees to action.

If you would like to leverage your past data to predict future events read on or watch <a href="http://www.youtube.com/watch?feature=player_embedded&v=c2WXsM-6M5k
" target="_blank">YOUTUBE Tutorial</a>: 




![alt text](https://github.com/SententiaInc/Salesforce_LogisticRegression/blob/master/SalesforceExample.PNG "Propensity Modeling")

In order to do the above you must have enough historical data and get it structured in the following manner. 

![alt text](https://github.com/SententiaInc/Salesforce_LogisticRegression/blob/master/sampledata.PNG "Propensity Modeling")

After you have the data - you will need to run "Logisitic Regression" in Excell (usually you need the data analysis toolpack to do so). 

There are plenty of videos out there to do so. 

After you run the regression - you will be given the computed coeffecients (e.g. Beta,wcBeta,raBeta,pcBeta below) and you will need to update salesforce similar to the example code below.  (Your data will vary, and your attributes will have different names). 

Then create a new formula field in salesforce (number) - and update it with similar code below to allow Salesforce to generate real-time predictive output. 


CODE: 

((EXP(
Beta +
IF( wcBeta * wasCalled__c, 1, 0) +
IF( raBeta *  receivedAd__c, 1, 0) + 
IF( pcBeta * priorCustomer__c, 1, 0) ))
/ (

1+ EXP(Beta +
IF( wcBeta * wasCalled__c, 1, 0) +
IF( raBeta *  receivedAd__c, 1, 0) + 
IF( pcBeta * priorCustomer__c, 1, 0) )))
)

NOTE: Please note - I mentioned atribute coefficient contribution being about equal to the percentage.   You can also see the amount change when you modify a live record (e.g. what the change in output is).   This generally allows you to see how much impact a particular attrubute has on client engagement.  Again - this is a very simple example on how to create dynamic fields and is not a comprehensive overview of logistic regression / propensity modeling.


![alt text](https://github.com/SententiaInc/Salesforce_LogisticRegression/blob/master/ColumnDef.PNG "Propensity Modeling")


