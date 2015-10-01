source("utils.R")

#dirs <- list.dirs("/run/media/gbecan/SeagateExtHD/gbecan/afm-synthesis/results_products")[-1]
#dirs <- c(dirs, list.dirs("/run/media/gbecan/SeagateExtHD/gbecan/afm-synthesis/results")[-1])
#dirs <- list.dirs("/home/gbecan/git/FOReverSE-AFMSynthesis-Evaluation/results_icse")[-1]
#dirs <- list.dirs("/home/gbecan/git/FOReverSE-AFMSynthesis-Evaluation/results_alternative")[-1]
#dirs <- list.dirs("/home/gbecan/git/FOReverSE-AFMSynthesis-Evaluation/results_both")[-1]
#dirs <- list.dirs("/mnt/windows/Users/gbecan/Documents/dev/results_optimized")[-1]
#dirs <- list.dirs("/home/gbecan/git/FOReverSE-AFMSynthesis-Evaluation/results_v2")[-1]
#dirs <- list.dirs("/home/gbecan/Documents/dev/AFM_Synthesis_grid5000/results_bestbuy-excerpt1")[-1]
dirs <- list.dirs("/home/gbecan/Documents/dev/AFM_Synthesis_grid5000/results_bestbuy")[-1]

print("Reading...")
files <- lapply(dirs, function(dir) readCSV(dir, "/metrics.csv"))
print("Merging...")
results <- Reduce(function(...) merge(..., all=T), files)
print("Saving...")
write.csv(results,"results_bestbuy.csv")
print("Done.")






