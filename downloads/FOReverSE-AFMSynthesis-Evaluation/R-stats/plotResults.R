source("utils.R")

results <- readCSV("", "results_optimized.csv")

count <- split(results, list(results$"#variables", results$"#configurations", results$"max domain size", results$"enable or groups"), drop=TRUE)
count <- lapply(count, function(x) nrow(x))
print(count[count < 100])

#### Shape of matrices ####
print("Edges in BIG")
print(summary(results$"#edges in BIG"))

print("Edges in mutex graph")
print(summary(results$"#edges in Mutex graph"))

#### Stats ####
print("RC")
print(summary(results$"#cross tree constraints"))

print("implies")
print(summary(results$"#implies"))

print("excludes")
print(summary(results$"#excludes"))

print("relation constraints")
print(summary(results$"#complex constraints"))


#### RQ1 ####

# Scalability over features
print("Scalability w.r.t features")
scalF <- results[results$"#configurations" == 1000 & results$"max domain size" == 10 & results$"enable or groups" == 'false',]
scalF$"Synthesis_plot" <-(scalF$"Synthesis"/1000)**(1/2)
plot(scalF$"#variables", scalF$"Synthesis_plot", xlab="Number of variables", ylab=expression(paste("Square root of time (", s**(1/2), ")")), cex=0.8, pch=18, xaxt="n")
axis(1, at = c(5, 50, 100, 200, 500, 1000, 2000), las=2)
meanScalF <- aggregate(scalF$"Synthesis_plot", by=list(scalF$"#variables"), FUN=mean)
points(meanScalF$"Group.1", meanScalF$"x", pch=21, col="red", bg="red")

points(scalF$"#variables", ((scalF$"Complex constraints"/1000)**(1/2)), cex=0.8, pch=18, col="blue", bg="blue")

scalF.lm <- lm(scalF$"Synthesis_plot" ~ scalF$"#variables")
scalF.r.squared <- summary(scalF.lm)$r.squared
print(scalF.r.squared)
abline(scalF.lm)
print(cor(scalF$"Synthesis_plot", scalF$"#variables"))

# Scalability over configurations
print("Scalability w.r.t configurations")
scalC <- results[results$"#variables" == 100 & results$"max domain size" == 10 & results$"enable or groups" == 'false',]
scalC$"Synthesis_plot" <- (scalC$"Synthesis" / 1000)
plot(scalC$"#distinct configurations", scalC$"Synthesis_plot", xlab="Number of configurations", ylab="Time (s)", cex=0.8, pch=18, xaxt="n")
axis(1, at = c(1000, 10000, 20000, 50000, 100000, 200000), las=2)
meanScalC <- aggregate(scalC$"Synthesis_plot", by=list(scalC$"#configurations"), FUN=mean)
points(meanScalC$"Group.1", meanScalC$"x", pch=21, col="red", bg="red")

#points(scalC$"#distinct configurations", (scalC$"Complex constraints"/1000), cex=0.8, pch=18, col="blue", bg="blue")

scalC.lm <- lm(scalC$"Synthesis_plot" ~ scalC$"#distinct configurations")
scalC.r.squared <- summary(scalC.lm)$r.squared
print(scalC.r.squared)
abline(scalC.lm)
print(cor(scalC$"Synthesis_plot", scalC$"#distinct configurations"))

# Scalability over max number of domain values
print("Scalability w.r.t max domain size")
scalD <- results[results$"#variables" == 10 & results$"#configurations" == 10000 & results$"enable or groups" == 'false',]
print(aggregate(scalD[,c("max domain size", "real max domain size")], by=list(scalD$"max domain size"), FUN=max))
scalD$"Synthesis_plot" <- ((scalD$"Synthesis" / 1000) ** (1/2))
plot(scalD$"real max domain size", scalD$"Synthesis_plot", xlab="Maximum domain size", ylab=expression(paste("Square root of time (", s**(1/2), ")")), cex=0.8, pch=18, xaxt="n")
axis(1, at = c(5, 200, 500, 1000, 2000, 5000, 10000), las=2)
meanScalD <- aggregate(data.frame(scalD$"Synthesis_plot", scalD$"real max domain size"), by=list(scalD$"max domain size"), FUN=mean)
points(meanScalD$"scalD..real.max.domain.size.", meanScalD$"scalD.Synthesis_plot", pch=21, col="red", bg="red")

#points(scalD$"real max domain size", ((scalD$"Complex constraints"/1000) ** (1/2)), cex=0.8, pch=18, col="blue", bg="blue")

scalD.lm <- lm(scalD$"Synthesis_plot" ~ scalD$"real max domain size")
scalD.r.squared <- summary(scalD.lm)$r.squared
print(scalD.r.squared)
abline(scalD.lm)
print(cor(scalD$"Synthesis_plot", scalD$"real max domain size"))


# Scalability over features with OR groups
scalFor <- results[results$"#configurations" == 1000 & results$"max domain size" == 10 & results$"enable or groups" == 'true',]
scalFor$"Or_plot" <- (scalFor$"Or" / (60*1000))
plot(scalFor$"#variables", scalFor$"Or_plot", xlab="Number of variables", ylab="Time (min)", cex=0.8, pch=18, xaxt="n", yaxt="n")
axis(1, at = c(5, 10, 15, 20, 25, 30, 35, 40, 50, 60, 70, 80, 90, 100), las=2)
axis(2, at = c(1, 5, 10, 15, 20, 25, 30, 35, 40), las=2)
meanScalFor <- aggregate(scalFor$"Or_plot", by=list(scalFor$"#variables"), FUN=mean)
points(meanScalFor$"Group.1", meanScalFor$"x", pch=21, col="red", bg="red")


# Bar plots
filteredTimes <- results[results$"enable or groups" == 'false',]
#filteredTimes <- results[results$"#variables" == 10 & results$"#configurations" == 10000 & results$"max domain size" >= 20 & results$"enable or groups" == 'false',]
#filteredTimes <- results[results$"#complex constraints" > 0,]
filteredTimes$"Feature and attribute extraction" <- filteredTimes$"Domain extraction" + filteredTimes$"Feature and attribute extraction"
filteredTimes$"Other steps" <- 
  filteredTimes$"Feature and attribute extraction" +
  filteredTimes$"Implication and Mutex graph" +
  filteredTimes$"Hierarchy" +
  filteredTimes$"Place attributes" +
  filteredTimes$"Mandatory features" +
  filteredTimes$"Feature groups" +
  #filteredTimes$"Mutex" +
  #filteredTimes$"Or" +
  #filteredTimes$"Xor" +
  filteredTimes$"Binary implies" + 
  filteredTimes$"Binary excludes" +
  filteredTimes$"AFM construction"
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


#### RQ2 ####
# overapprox <- na.omit(results)
# overapprox <- (overapprox$"#output configurations" - overapprox$"#distinct configurations") / overapprox$"#output configurations"
# plot(ecdf(overapprox), xpd=FALSE, xlab="Overapproximation", ylab="Distribution", main="")