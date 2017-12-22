g++ script_inputfile/script.cpp -std=c++11
./a.out $1
./FSG_pafi/fsg -s 40.0 inputfile.txt

javac ctree/chem/TranslateFSG.java
java ctree/chem/TranslateFSG inputfile.fp freq_subgraph.txt
javac ctree/toolbox/subgraphFrequencyCounter.java
java ctree/toolbox/subgraphFrequencyCounter $1 $4 freq_subgraph.txt $2 $3
