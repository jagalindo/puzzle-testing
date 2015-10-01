readCSV <- function(dir,path) {
  fullPath = paste(dir,path,sep="")
  if (file.exists(fullPath)) {
    read.csv(fullPath,check.name=FALSE) 
  } else {
    NULL
  }
}