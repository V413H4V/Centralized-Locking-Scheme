# Centralized Locking Scheme

#### Implementation of Centralized Locking scheme to regulate shared file-access in a Distributed System.
===============================================================================
### Usage:
- #### Script files (Run.bat and run.sh) are used just to run all the processes simulteneously to simulate real-world distributed systems. It contains no-other code than this.
#### •	For Windows:
  - #### Just double-click 'Run.bat' file or open Command Prompt and navigate to the directory containing Jars (Coordinator.jar, Process1.jar etc). Open Run.bat file.
  - > ` Run.bat `
#### •	For Linux:
  - #### Open the Terminal and navigate to the directory containing Jars (Coordinator.jar, Process1.jar etc). Open run.sh file.
  - > ` ./run.sh `
  ####
  ### Description:
- #### Different nodes in a distributed system are emulated by different processes running simulteneously. 
#####
- #### Coordinator node regulates the shared file-access.
#####
- #### When a process acquires the lock, it simply opens the file, increments a counter in the file, and closes the file. Assume that all processes keep requesting the lock until successfully acquiring the lock.
#####
####
### Output:
##### 
![Output](Centralized-Locking-Scheme.PNG?raw=true)
#####
