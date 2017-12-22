#include <iostream>
#include <fstream>
#include <sstream>
using namespace std;

int main(int argc, const char * argv[]) {
    
    string str;
    string input_file = argv[1];
    
    ifstream infile;
    infile.open(input_file);
    
    ofstream outputfile("inputfile.txt");
    
    int num,label1,label2,e_label;
    int v_i;
    
    while(getline(infile,str)){
        
        outputfile<<"t "<<"# "<<str.substr(1,str.length())<<endl;
        infile>>num;
//        cout<<num<<endl;
        v_i=0;
        while(v_i!=num){
            infile>>str;
            outputfile<<"v "<<v_i<<" "<<str<<endl;
            v_i++;
        }
        infile>>num;
//        cout<<endl<<num<<endl;
        getline(infile,str);
        while(num--){
            getline(infile,str);
            istringstream iss(str);
            iss>>label1>>label2>>e_label;
            outputfile<<"u "<<label1<<" "<<label2<<" "<<e_label<<endl;
        }
//        cout<<"here3"<<endl;
    }
    
    
    
    return 0;
}
