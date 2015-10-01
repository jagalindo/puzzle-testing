source("utils.R")

results <- readCSV("", "results_both.csv")

# v 1000 10
# 50 c 10
# 10 10000 d

count <- split(results, list(results$"#variables", results$"#configurations", results$"max domain size", results$"enable or groups"), drop=TRUE)
count <- lapply(count, function(x) nrow(x))
print(count[count < 100])

diff <- results$"Sicstus" - results$"alternative"
diff <- diff / 1000
factor <- results$"Sicstus" / results$"alternative" 

boxplot((results$"alternative" / 1000), (results$"Sicstus" / 1000), horizontal=TRUE, names=c("alternative", "sicstus"))

print("Sicstus (s)")
print(summary(results$"Sicstus" / 1000))
print("Alternative (s)")
print(summary(results$"alternative" / 1000))

print("Diff (Sicstus - alternative)")
print(summary(diff))
print("Improvement")
print(summary(factor))

# Scalability w.r.t number of variables
scalV <- results[
    results$"#configurations" == 1000 & 
    results$"max domain size" == 10 & 
    results$"enable or groups" == 'false'
  ,]
plot(scalV$"#variables", (scalV$"Sicstus" / 1000) ** (1/2), pch=18, col="black", bg="black", main="variables")
points(scalV$"#variables", (scalV$"alternative" / 1000) ** (1/2), pch=18, col="red", bg="red")

# Scalability w.r.t number of configurations
scalC <- results[
    results$"#variables" == 50 & 
    results$"max domain size" == 10 & 
    results$"enable or groups" == 'false'
  ,]
plot(scalC$"#distinct configurations", (scalC$"Sicstus" / 1000), pch=18, col="black", bg="black", main="configurations")
points(scalC$"#distinct configurations", (scalC$"alternative" / 1000), pch=18, col="red", bg="red")

# Scalability w.r.t maximum domain size
scalD <- results[
    results$"#variables" == 10 & 
    results$"#configurations" == 10000 & 
    results$"enable or groups" == 'false'
  ,]
plot(scalD$"real max domain size", (scalD$"Sicstus" / 1000), pch=18, col="black", bg="black", main="domain size")
points(scalD$"real max domain size", (scalD$"alternative" / 1000), pch=18, col="red", bg="red")

# Comparison
plot((results$"Sicstus" / 1000), pch=18, col="black", bg="black")
points((results$"alternative" / 1000), pch=18, col="red", bg="red")
