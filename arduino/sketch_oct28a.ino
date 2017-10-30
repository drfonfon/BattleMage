#define numleds 4
#define pins 2 

int RED[numleds]; 
int GREEN[numleds]; 
int BLUE[numleds]; 

int clockpins[pins]; 
int datapins[pins];

int inS = 0;
int outS = 0;

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

void setColor(int i) {
  switch(i) {
    case 0:
    break;
    case 1:
    break;
    case 2:
    break;
    case 3:
    break;
    case 4:
    break;
  }
}

void setColor(int r, int g, int b) {
  for (int i = 0; i < numleds; i = i++){ 
    RED[i]=r; 
    GREEN[i]=g; 
    BLUE[i]=b; 
  } 
}

void updateString() { 
  for (int j =0; j<pins; j++){ 
    for (int i =0; i<numleds; i++){ 
      shiftOut(datapins[j], clockpins[j], MSBFIRST, BLUE[i]); 
      shiftOut(datapins[j], clockpins[j], MSBFIRST, GREEN[i]); 
      shiftOut(datapins[j], clockpins[j], MSBFIRST, RED[i]); 
    } 
   } 
}

void loop() { 
for(int i=0;i<8;i++)
{
RED[i]=255;GREEN[i]=255;BLUE[i]=255;
updateString();
RED[i]=0;GREEN[i]=0;BLUE[i]=0;
delay(300);
}
} 


