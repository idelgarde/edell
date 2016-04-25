int buffer;
#define  a1 3
#define  a2 5
#define  a3 7
#define  a4 9


void setup() {
  Serial.begin(9600);    
  pinMode(a1, OUTPUT); 
  pinMode(a2, OUTPUT);  
  pinMode(a3, OUTPUT);    
  pinMode(a4, OUTPUT);

}


void loop() {
  
  if(Serial.available()){
    buffer=Serial.read();
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
    case 2:
      digitalWrite(a4,LOW);
      digitalWrite(a3,HIGH);
      break;
    case 5:
      digitalWrite(a2,LOW);
      digitalWrite(a1,LOW);
      break;
    case 0:
      digitalWrite(a4,LOW);
      digitalWrite(a3,LOW);
      digitalWrite(a1,LOW);
      digitalWrite(a2,LOW);
      break;
    }
  }
}

