#define numleds 24
#define pins 2 

int RED[numleds];
int GREEN[numleds];
int BLUE[numleds];

int clockpins[pins]; 
int datapins[pins];

int redIn = 0;
int greenIn = 0;
int blueIn = 0;
int upIn = 0;
int downIn = 0;
int loopIn = 0;

int curUpIn = 0;
int curDownIn = 0;
int curLoopIn = 0;

int curR = 0;
int curG = 0;
int curB = 0;

bool newData = true;

void setup() {
  clockpins[0] = 2; 
  clockpins[1] = 4; 
  datapins[0] = 3; 
  datapins[1]= 5; 
  for(int i =0; i< pins; i++) { 
    pinMode(clockpins[i], OUTPUT); 
    pinMode(datapins[i], OUTPUT); 
  } 
  setColor(0,0,0);
  Serial.begin(9600);
}

void setColor(int r, int g, int b) {
  for (int i = 0; i<numleds; i++){ 
    RED[i]=r; 
    GREEN[i]=g; 
    BLUE[i]=b; 
  } 
}

void updateString() { 
  for (int j =0; j< pins; j++){ 
    for (int i =0; i< numleds; i++){ 
      shiftOut(datapins[j], clockpins[j], MSBFIRST, RED[i]); 
      shiftOut(datapins[j], clockpins[j], MSBFIRST, BLUE[i]); 
      shiftOut(datapins[j], clockpins[j], MSBFIRST, GREEN[i]); 
    } 
   } 
}

int nextColor(int cur, int m, int colorMax) {
  return colorMax - (colorMax * cur)/ m;
}

int prevColor(int cur, int m, int colorMax) {
  return (colorMax * cur)/ m;
}

void loop() {
 recvOneChar();
 showNewData();
 delay(100);
}

void setColors(int i) {
  switch(i){
    case 0:
      redIn = 0;
      greenIn = 0;
      blueIn = 0;
      break;
    case 1:
       redIn = 0;
       greenIn = 0;
       blueIn = 255;
      break;
    case 2:
       redIn = 0;
       greenIn = 255;
       blueIn = 0;
      break;
    case 3:
       redIn = 0;
       greenIn = 255;
       blueIn = 255;
      break;
    case 4:
       redIn = 255;
       greenIn = 0;
       blueIn = 0;
      break;
    case 5:
       redIn = 255;
       greenIn = 0;
       blueIn = 255;
      break;
    case 6:
       redIn = 255;
       greenIn = 255;
       blueIn = 0;
      break;
    case 7:
       redIn = 255;
       greenIn = 255;
       blueIn = 255;
      break;
    
  }
}

byte bytes[4]; 

void recvOneChar() {
 if (Serial.available() > 0) {
    Serial.readBytes(bytes, 4);
    setColors(bytes[0]);
    upIn = bytes[1];
    downIn = bytes[2];
    loopIn = bytes[3];

    curUpIn = upIn;
    curDownIn = downIn;
    curLoopIn = loopIn;
    newData = true;
  }
}

void showNewData() {
 if (newData == true) {
  if(curLoopIn > 0) {
    if(curUpIn > 0) {
      curUpIn--;
      curR = nextColor(curUpIn, upIn, redIn);
      curG = nextColor(curUpIn, upIn, greenIn);
      curB = nextColor(curUpIn, upIn, blueIn);
    } else if(curDownIn > 0) {
        curDownIn--;
        curR = prevColor(curDownIn, downIn, redIn);
        curG = prevColor(curDownIn, downIn, greenIn);
        curB = prevColor(curDownIn, downIn, blueIn);
    } else {
      curR = 0;
      curG = 0;
      curB = 0;
      curLoopIn--;
      curUpIn = upIn;
      curDownIn = downIn;
    }
  } else {
    newData = false;
  }
 } else {
  curR = 0;
  curG = 0;
  curB = 0;
 }
 setColor(curR, curG, curB);
 updateString();
}

