source("utils.R")

results <- readCSV("", "results_bestbuy.csv")

count <- split(results, list(results$"#variables", results$"#configurations", results$"max domain size", results$"enable or groups"), drop=TRUE)
count <- lapply(count, function(x) nrow(x))
#print(count[count < 100])



#results$"#variables" = results$"#features" + results$"#attributes"

#### Shape of matrices ####
print("Edges in BIG")
print(summary(results$"#edges in BIG"))

print("Edges in mutex graph")
print(summary(results$"#edges in Mutex graph"))

#### Dataset ####
print("Matrices")
print(nrow(results))

print("Variables")
print(summary(results$"#variables"))

print("Configurations")
print(summary(results$"#configurations"))

print("Max domain size")
print(summary(results$"max domain size"))

print("Empty cells")
statsBestBuy <- readCSV("", "stats_bestbuy.csv")
print(summary(statsBestBuy$"percentage of N/A") * 100)

#### Synthesis ####
print("Synthesis")
print(summary(results$"Synthesis") / 1000)

print("Synthesis (no or-groups)")
print(summary((results$"Synthesis" - results$"Or") / 1000))

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
scalF <- results
scalF$"Synthesis_plot" <-(scalF$"Synthesis"/1000)**(1/2)
plot(scalF$"#variables", scalF$"Synthesis_plot", xlab="Number of variables", ylab=expression(paste("Square root of time (", s**(1/2), ")")), cex=0.8, pch=18, xaxt="n")
axis(1, at = c(5, 50, 100, 200, 500, 1000, 2000), las=2)
#meanScalF <- aggregate(scalF$"Synthesis_plot", by=list(scalF$"#variables"), FUN=mean)
#points(meanScalF$"Group.1", meanScalF$"x", pch=21, col="red", bg="red")




# Bar plots
filteredTimes <- results
#filteredTimes <- results[results$"#variables" == 10 & results$"#configurations" == 10000 & results$"max domain size" >= 20 & results$"enable or groups" == 'false',]
#filteredTimes <- results[results$"#complex constraints" > 0,]
filteredTimes$"Feature and attribute extraction" <- filteredTimes$"Domain extraction" + filteredTimes$"Feature and attribute extraction"
filteredTimes$"Other steps" <- 
  filteredTimes$"Feature and attribute extraction" +
  filteredTimes$"Implication and Mutex graph" +
  filteredTimes$"Hierarchy" +
  filteredTimes$"Place attributes" +
  filteredTimes$"Mandatory features" +
  #filteredTimes$"Feature groups" +
  filteredTimes$"Mutex" +
  #filteredTimes$"Or" +
  filteredTimes$"Xor" +
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
  "Or", 
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