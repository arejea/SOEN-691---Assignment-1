library(caret)
library(Hmisc)
library(rms)
library(mdscore)

###Clear Environment
rm(list = ls(all.names = TRUE))

data_understand<-read.csv("/Users/wedad/Courses/Winter2020/DevOps/Project/All_Metrics/final_data/hadoop_metrics_from_understand.csv")
data_change_metrics<-read.csv("/Users/wedad/Courses/Winter2020/DevOps/Project/All_Metrics/final_data/hadoop_all_change_metrics.csv")
data_anti_patterns<-read.csv("/Users/wedad/Courses/Winter2020/DevOps/Project/All_Metrics/final_data/anti_pattern_metric.csv")
data_flow_handling<-read.csv("/Users/wedad/Courses/Winter2020/DevOps/Project/All_Metrics/final_data/flow_handling_metrics.csv")

merged_data1<- merge(data_understand, data_change_metrics,by="JavaFile")
merged_data2<- merge(data_anti_patterns, data_flow_handling, by="JavaFile")
merged_data<- merge(merged_data1, merged_data2, by="JavaFile")
write.csv(merged_data, "/Users/wedad/Courses/Winter2020/DevOps/Project/All_Metrics/final_data/dataset.csv")


###### Creating the BASE model with only the traditional software metrics
###### and without the metrics that are associated with exception handling practices
data = merged_data
drop=c("JavaFile")
data = data[,!(names(data) %in% drop)]
drop=c(names(data_anti_patterns))
data = data[,!(names(data) %in% drop)]
drop=c(names(data_flow_handling))
data = data[,!(names(data) %in% drop)]
drop=c("PostReleaseBugs")
independant=data[,!(names(data) %in% drop)]

# Remove the metrics that have high corelation with each other using cutoff 0.80 (MC4)
correlations <- cor(independant, method="spearman") 
highCorr <- findCorrelation(correlations, cutoff = .80)

# Prepare the data for redundancy analysis
low_cor_names=names(independant[, -highCorr])
low_cor_data= independant[(names(independant) %in% low_cor_names)]
dataforredun=low_cor_data
names(low_cor_data)

# Remove redundant metrics (MC5)
redun_obj = redun (~. ,data = dataforredun ,nk =0)
after_redun= dataforredun[,!(names(dataforredun) %in% redun_obj $Out)]

# Build our formula to use in the regression model and then we build the model
form=as.formula(paste("PostReleaseBugs>0~",paste(names(after_redun),collapse="+")))
model=glm(formula=form, data=log10(data+1), family = binomial(link = "logit"))
summary(model)

# We check summary of the model to find more significant metrics then we update our formula and model till all metrics in the model are significant
newform=PostReleaseBugs>0~CountDeclClassVariable+CountLineComment+RatioCommentToCode+TotalNumOfChanges+PreReleaseBugs
newmodel=glm(formula=newform, data=log10(data+1), family = binomial(link = "logit"))
summary(newmodel)

# Calculate R2
R2_BASE_model = 1-newmodel$deviance/newmodel$null.deviance
R2_BASE_model

####### Creating the BSFC model by adding software metrics that are associated with 
####### quantified exception flow characteristics to the traditional software metrics
data = merged_data
drop=c("JavaFile")
data = data[,!(names(data) %in% drop)]
drop=c(names(data_anti_patterns))
data = data[,!(names(data) %in% drop)]
drop=c("PostReleaseBugs")
independant=data[,!(names(data) %in% drop)]

# Remove the metrics that have high corelation with each other using cutoff 0.80 (MC4)
correlations <- cor(independant, method="spearman") 
highCorr <- findCorrelation(correlations, cutoff = .80)

# Prepare the data for redundancy analysis
low_cor_names=names(independant[, -highCorr])
low_cor_data= independant[(names(independant) %in% low_cor_names)]
dataforredun=low_cor_data
names(low_cor_data)

# Remove redundant metrics (MC5)
redun_obj = redun (~. ,data = dataforredun ,nk =0)
after_redun= dataforredun[,!(names(dataforredun) %in% redun_obj $Out)]

# Build our formula to use in the regression model and then we build the model
form=as.formula(paste("PostReleaseBugs>0~",paste(names(after_redun),collapse="+")))
model=glm(formula=form, data=log10(data+1), family = binomial(link = "logit"))
summary(model)

# We check summary of the model to find more significant metrics then we update our formula and model till all metrics in the model are significant
newform=PostReleaseBugs>0~CountDeclClassVariable+CountLineComment+RatioCommentToCode+TotalNumOfChanges+PreReleaseBugs+ThrowWrapPercent+TryCallDepth
newmodel=glm(formula=newform, data=log10(data+1), family = binomial(link = "logit"))
summary(newmodel)

# Calculate R2
R2_BSFC_model =  1-newmodel$deviance/newmodel$null.deviance
R2_BSFC_model

# we calculate the model outcome by setting all predictors at their mean value
# next we increase each significant predictor's value by 10% while keeping all other significant predictors at their mean values
# then we calculate model outcome as the effect of the predictor
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    ThrowWrapPercent=log10(mean(data$ThrowWrapPercent)+1),
                    TryCallDepth=log10(mean(data$TryCallDepth)+1))
predict(newmodel,testdata, type="response")
# Effect of CountDeclClassVariable
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)*1.1+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    ThrowWrapPercent=log10(mean(data$ThrowWrapPercent)+1),
                    TryCallDepth=log10(mean(data$TryCallDepth)+1))
predict(newmodel,testdata, type="response")
# Effect of CountLineComment
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)*1.1+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    ThrowWrapPercent=log10(mean(data$ThrowWrapPercent)+1),
                    TryCallDepth=log10(mean(data$TryCallDepth)+1))
predict(newmodel,testdata, type="response")
# Effect of RatioCommentToCode 
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)*1.1+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    ThrowWrapPercent=log10(mean(data$ThrowWrapPercent)+1),
                    TryCallDepth=log10(mean(data$TryCallDepth)+1))
predict(newmodel,testdata, type="response")
# Effect of TotalNumOfChanges
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)*1.1+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    ThrowWrapPercent=log10(mean(data$ThrowWrapPercent)+1),
                    TryCallDepth=log10(mean(data$TryCallDepth)+1))
predict(newmodel,testdata, type="response")
# Effect of PreReleaseBugs
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)*1.1+1),
                    ThrowWrapPercent=log10(mean(data$ThrowWrapPercent)+1),
                    TryCallDepth=log10(mean(data$TryCallDepth)+1))
predict(newmodel,testdata, type="response")
# Effect of ThrowWrapPercent
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    ThrowWrapPercent=log10(mean(data$ThrowWrapPercent)*1.1+1),
                    TryCallDepth=log10(mean(data$TryCallDepth)+1))
predict(newmodel,testdata, type="response")
# Effect of TryCallDepth
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    ThrowWrapPercent=log10(mean(data$ThrowWrapPercent)+1),
                    TryCallDepth=log10(mean(data$TryCallDepth)*1.1+1))
predict(newmodel,testdata, type="response")

###### Creating the BSAP model by adding software metrics that are associated with
###### exception handling anti-patterns to the traditional software metrics
data = merged_data
drop=c("JavaFile")
data = data[,!(names(data) %in% drop)]
drop=c(names(data_flow_handling))
data = data[,!(names(data) %in% drop)]
drop=c("PostReleaseBugs")
independant=data[,!(names(data) %in% drop)]

# Remove the metrics that have high corelation with each other using cutoff 0.80 (MC4)
correlations <- cor(independant, method="spearman") 
highCorr <- findCorrelation(correlations, cutoff = .80)

# Prepare the data for redundancy analysis
low_cor_names=names(independant[, -highCorr])
low_cor_data= independant[(names(independant) %in% low_cor_names)]
dataforredun=low_cor_data
names(low_cor_data)

# Remove redundant metrics (MC5)
redun_obj = redun (~. ,data = dataforredun ,nk =0)
after_redun= dataforredun[,!(names(dataforredun) %in% redun_obj $Out)]

# Build our formula to use in the regression model and then we build the model
form=as.formula(paste("PostReleaseBugs>0~",paste(names(after_redun),collapse="+")))
model=glm(formula=form, data=log10(data+1), family = binomial(link = "logit"))
summary(model)

# We check summary of the model to find more significant metrics then we update our formula and model till all metrics in the model are significant
newform=PostReleaseBugs>0~CountDeclClassVariable+CountLineComment+RatioCommentToCode+TotalNumOfChanges+PreReleaseBugs+CatchSLOC
newmodel=glm(formula=newform, data=log10(data+1), family = binomial(link = "logit"))
summary(newmodel)

# Calculate R2
R2_BSAP_model = 1-newmodel$deviance/newmodel$null.deviance
R2_BSAP_model
# we calculate the model outcome by setting all predictors at their mean value
# next we increase each significant predictor's value by 10% while keeping all other significant predictors at their mean values
# then we calculate model outcome as the effect of the predictor
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    CatchSLOC=log10(mean(data$CatchSLOC)+1))
predict(newmodel,testdata, type="response")
# Effect of CountDeclClassVariable
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)*1.1+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    CatchSLOC=log10(mean(data$CatchSLOC)+1))
predict(newmodel,testdata, type="response")
# Effect of CountLineComment
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)*1.1+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    CatchSLOC=log10(mean(data$CatchSLOC)+1))
predict(newmodel,testdata, type="response")
# Effect of RatioCommentToCode
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)*1.1+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    CatchSLOC=log10(mean(data$CatchSLOC)+1))
predict(newmodel,testdata, type="response")
# Effect of TotalNumOfChanges
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)*1.1+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    CatchSLOC=log10(mean(data$CatchSLOC)+1))
predict(newmodel,testdata, type="response")
# Effect of PreReleaseBugs
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)*1.1+1),
                    CatchSLOC=log10(mean(data$CatchSLOC)+1))
predict(newmodel,testdata, type="response")
# Effect of CatchSLOC
testdata=data.frame(CountDeclClassVariable=log10(mean(data$CountDeclClassVariable)+1),
                    CountLineComment=log10(mean(data$CountLineComment)+1), 
                    RatioCommentToCode=log10(mean(data$RatioCommentToCode)+1),
                    TotalNumOfChanges=log10(mean(data$TotalNumOfChanges)+1),
                    PreReleaseBugs=log10(mean(data$PreReleaseBugs)+1),
                    CatchSLOC=log10(mean(data$CatchSLOC)*1.1+1))
predict(newmodel,testdata, type="response")


