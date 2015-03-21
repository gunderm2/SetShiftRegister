/*
*The MIT License (MIT)
*
*Copyright (c) 2015 Michael Gunderson
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in
*all copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
*THE SOFTWARE.
 */
package ioio.examples.simple;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class IOIOSimpleApp extends IOIOActivity {
	private TextView	txtView;
	private EditText 	binNumInput;
	private Button 		btnGo;
	public boolean		btnGoPressed = false;
	public String       strBinValue = "";
	public Boolean[]    sInValue = new Boolean[8];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secondview);
		txtView = (TextView) findViewById(R.id.error);
		binNumInput = (EditText) findViewById(R.id.binNumInput);
		btnGo = (Button) findViewById(R.id.btnGo);
		btnGo.setOnClickListener(new View.OnClickListener() {	
            public void onClick(View view) {
            	boolean allOneOrZeroNums = true;
            	txtView.setText("");
            	strBinValue = binNumInput.getText().toString(); 
            	System.out.println(strBinValue.length());
            	if(strBinValue.length() == 8) {
					for(int i = 0; i < sInValue.length; i++) {
						if(strBinValue.charAt(i) == '1') {						
							sInValue[i] = true;
			            } else if (strBinValue.charAt(i) == '0'){
			            	sInValue[i] = false;
			            } else {
			            	allOneOrZeroNums = false;
			            	txtView.setText("You must enter 8 1's or 0's no other number or letter will work!");
			            	break;
			            }
					}				
	            	if(allOneOrZeroNums) btnGoPressed = true;  
            	} else {
            		allOneOrZeroNums = false;
	            	txtView.setText("You must enter 8 1's or 0's no other number or letter will work!");
            	}
            }
        });
	}

	class Looper extends BaseIOIOLooper {
		private DigitalOutput sIn;  
		private DigitalOutput sClk;
		private DigitalOutput rClk;
		
		@Override
		public void setup() throws ConnectionLostException, InterruptedException {
			
			int sin = 4; 
			int sclk = 3;
			int rclk = 2;
			// Ready each pin for use as an output.
			sIn = ioio_.openDigitalOutput(sin, false);
			sClk = ioio_.openDigitalOutput(sclk, true);
			rClk = ioio_.openDigitalOutput(rclk, true);
		}

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			if(btnGoPressed) {	
				initButton();//set button back to false we only want to update one 8 bit number at a time
				for(int y = 0; y < sInValue.length; y++) {
					sIn.write(sInValue[y]);//first set the shift registers value you want to set
					Thread.sleep(20);
					
					rClk.write(false);//now bring the register clock low
					Thread.sleep(20);
					
					sClk.write(false);//now bring the register clock low
					Thread.sleep(20);
					
					sClk.write(true);//toggling back up bring the shift clock back high at its resting state
					Thread.sleep(20);
					
					rClk.write(true);//finish the toggle of the register clock and bring it back high at its resting state
					Thread.sleep(20);
				}
			}
		    // Don't call this loop again for 100 milliseconds
		    Thread.sleep(1000);
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
	
	private void initButton() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnGoPressed = false;
			}
		});
	}
}
