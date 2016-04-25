#include <SoftwareSerial.h> 
int a1=3;
int a2=5;
int a3=7;
int a4=9;
int buffer;
SoftwareSerial bt(10, 11);

void setup() {
  bt.begin(9600);      // initialization UART
  pinMode(a1, OUTPUT);    // additional channel
  pinMode(a2, OUTPUT);      // output for motor rotation
  pinMode(a3, OUTPUT);      // output for motor rotation
  pinMode(a4, OUTPUT);

}


void loop() {
  
  if(bt.available()){
    buffer=bt.read();
    switch(buffer){
    case 4:
      digitalWrite(a1,HIGH);
      digitalWrite(a2,LOW);
      break;
    case 6:
      digitalWrite(a2,HIGH);
      digitalWrite(a1,LOW);
      break;
    case 8:
      digitalWrite(a3,LOW);
      digitalWrite(a4,HIGH);
      break;
    case 5:
      digitalWrite(a4,LOW);
      digitalWrite(a3,HIGH);
      break;
    case 3:
      digitalWrite(a4,LOW);
      digitalWrite(a3,LOW);
      digitalWrite(a1,LOW);
      digitalWrite(a2,LOW);
      break;
    }
  }
}
