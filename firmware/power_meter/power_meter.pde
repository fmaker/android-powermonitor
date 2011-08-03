#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define  CURR A0
#define  VOLT A1

AndroidAccessory acc("Google, Inc.",
		     "PowerMonitor",
		     "Power Monitor",
		     "0.9", 
		     "http://www.android.com",
		     "504F5745524D4F4E");  /* POWERMON in ASCII */

unsigned long time;
uint16_t volt, curr;
boolean conn = false;

#define DEBUG  1

void setup() {
  Serial.begin(115200);
  //analogReference(INTERNAL1V1);
  acc.powerOn();
}

void loop() {
  
  if(acc.isConnected()){
    conn = true;
  
    /* Read timestamp and value */
    time = micros();
    curr = analogRead(CURR);
    volt = analogRead(VOLT);
  
#ifdef DEBUG
//  Serial.print("Size of messages: ");                                                                                                                                                                                                                                        
//  Serial.println(sizeof(time) + sizeof(val));
    Serial.print(time, HEX);
    Serial.print(' ');
    Serial.print(curr, DEC);
    Serial.print(' ');
    Serial.print(volt, DEC);
    Serial.print('\n');
#endif
    
    /* Send timestamp and value to Android */
    acc.write(&time,sizeof(time));
    acc.write(&curr,sizeof(curr));
    acc.write(&volt,sizeof(volt));
    delay(100);
  }
  else{
    conn = false;
  }
  
}

