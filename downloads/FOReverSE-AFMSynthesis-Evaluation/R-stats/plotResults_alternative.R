source("utils.R")

results <- readCSV("", "results_alternative.csv")

count <- split(results, list(results$"#variables", results$"#configurations", results$"max domain size", results$"enable or groups"), drop=TRUE)
count <- lapply(count, function(x) nrow(x))
print(count[count < 100])

# Scalability over max number of domain values
print("Scalability w.r.t max domain size")
scalD <- results[results$"#variables" == 10 & results$"#configurations" == 10000 & results$"enable or groups" == 'false' & results$"#distinct configurations" == 10000,]
scalD <- scalD[,c("real max domain size", "Binary implications", "max domain size")]
#scalD <- scalD[scalD$"max domain size" <= 5000,]
scalD <- data.frame(scalD[1], lapply(scalD[2], function(x) (x/1000)), scalD[3], check.names = FALSE)
plot(scalD$"real max domain size", scalD$"Binary implications", xlab="Maximum domain size", ylab="Time (s)", cex=0.8, pch=18, xaxt="n")
axis(1, at = c(5, 200, 500, 1000, 2000, 5000, 10000), las=2)
meanScalD <- aggregate(data.frame(scalD$"Binary implications", scalD$"real max domain size"), by=list(scalD$"max domain size"), FUN=mean)
points(meanScalD$"scalD..real.max.domain.size.", meanScalD$"scalD..Binary.implications.", pch=21, col="red", bg="red")

scalD.lm <- lm(scalD$"Binary implications" ~ scalD$"real max domain size")
scalD.r.squared <- summary(scalD.lm)$r.squared
print(scalD.r.squared)
#abline(scalD.lm)

# Bar plots
filteredTimes <- results[results$"max domain size" > 500 & results$"enable or groups" == 'false',]
filteredTimes$"Feature and attribute extraction" <- filteredTimes$"Domain extraction" + filteredTimes$"Feature and attribute extraction"
filteredTimes$"Other steps" <- 
  filteredTimes$"Feature and attribute extraction" + 
  filteredTimes$"Implication and Mutex graph" + 
  filteredTimes$"Hierarchy" +
  filteredTimes$"Place attributes" +
  filteredTimes$"Mandatory features" +
  filteredTimes$"Feature groups" +
  filteredTimes$"Binary implies" + 
  filteredTimes$"Binary excludes"
times <- filteredTimes[, c(
  #"Feature and attribute extraction", 
  "Binary implications", 
  #"Implication and Mutex graph", 
  #"Hierarchy", 
  #"Place attributes", 
  #"Mandatory features", 
  #"Mutex", 
  #"Or", 
  #"Xor", 
  #"Group processing", 
  #"Feature groups",
  #"Binary implies", 
  #"Binary excludes", 
  "Complex constraints",
  #"Cross tree constraints",
  #"AFM construction",
  "Other steps"
)]

times <- as.matrix(lapply(times, function(x) mean(x/filteredTimes$"Synthesis")))
barplot(times,
        xaxt="n",
        #xlab="Percentage of the total synthesis duration", 
        col=c("grey", "lightgrey","darkgrey"),
        #col=c("red","green"),
        #legend = row.names(times), 
        #args.legend = list(x = "topright", bty = "n", inset=c(-0.15, 0)),
        horiz=TRUE, 
        beside=FALSE
)
axis(1, at = seq(0, 1, 0.1), las=2)
legend("topleft",row.names(times), fill=c("grey", "lightgrey","darkgrey")) 