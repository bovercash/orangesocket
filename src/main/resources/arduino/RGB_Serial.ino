/**
 * Serial Command Syntax
 * /0-2 = Component Identifier (for if we add more LEDs)
 * /'#########' where positions 0-2 are Red value, 3-5 are Green, and 6-8 are Blue 
 * Example D01080200125 means to set the RGB value of compoent D01 to 80,200,125
 */
 
String currentCmd = "";
boolean newCmd = false;

int D01[] = {11,10,9};  //LED 01

void setup(){
  Serial.begin(9600);
  pinMode(D01[0],OUTPUT);
  pinMode(D01[1],OUTPUT);
  pinMode(D01[2],OUTPUT);
  Serial.println("Started Up");
}

void loop(){
  if(newCmd){
    Serial.print("Got New Command: ");
    Serial.println(currentCmd);
    if(currentCmd.equals("PING")){
      Serial.println("PONG");
      lightShow(D01);
      currentCmd = "";
      newCmd = false;
    }else if(currentCmd.startsWith("D01")){
      int red   = currentCmd.substring(3,6).toInt();
      int green = currentCmd.substring(6,9).toInt();
      int blue  = currentCmd.substring(9,12).toInt();
      Serial.print("Red:");
      Serial.println(red,DEC);
      Serial.print("Green:");
      Serial.println(green,DEC);
      Serial.print("Blue:");
      Serial.println(blue,DEC);
      setColor(D01,red,green,blue);
      currentCmd = "";
      newCmd = false;
    }else{
      Serial.println("UNKNOWN COMMAND");
      currentCmd = "";
      newCmd = false;
    }
  }
}

void lightShow(int component[]){
  setColor(D01,255,0,0);
  delay(200);
  setColor(D01,0,255,0);
  delay(200);
  setColor(D01,0,0,255);
  delay(200);
  setColor(D01,0,0,0);
}

void serialEvent() {
  if(!newCmd){
    while (Serial.available()) {
      int nextByte = Serial.read();
      if (nextByte == 3) {
        //ETX
        newCmd = true;
      }else if(nextByte == 4){
        //EOT
        currentCmd = "";
      }else{
        currentCmd += (char)nextByte;
      }
    }
  }
}

void setColor(int component[], int red, int green, int blue){
  #ifdef COMMON_ANODE
    red = 255 - red;
    green = 255 - green;
    blue = 255 - blue;
  #endif
  analogWrite(component[0], red);
  analogWrite(component[1], green);
  analogWrite(component[2], blue);  
}

