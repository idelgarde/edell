char leitura;
#define a1 8

void setup(){
Serial.begin(9600);
pinMode(a1, OUTPUT);
digitalWrite(a1, LOW);
}

void loop()
{
  
while(Serial.available()>0){
  leitura  = Serial.read();
  if(leitura==0){
    digitalWrite(a1, HIGH);
  }
  else if(leitura==1){
    digitalWrite(a1, LOW);
  }
}
}
