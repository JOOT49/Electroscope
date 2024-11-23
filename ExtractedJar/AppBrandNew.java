package com.mygdx.spectrometer; // change this name

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static java.lang.Math.*;
import static java.lang.Math.asin;

public class spectrometer extends ApplicationAdapter {

	boolean app = false;
	SpriteBatch batch;
	private GlyphLayout layout = new GlyphLayout();
	private Viewport viewport;
	private Camera camera;
	private ShapeRenderer shapeRenderer;
	float SCREENX, SCREENY, MIDX, MIDY, BUTX, BUTY, PI, WSLIDER, RBUT, mx, my, TD, SLIDERX;
	boolean touch = false;
	boolean rTouch = false;

	private float sin(float val){
		return (float)MathUtils.sin(val);
	}

	private float cos(float val){
		return (float)MathUtils.cos(val);
	}

	private float tan(float val){
		return (float)Math.tan(val);
	}

	private float ran(float x1, float x2){
		return (float)(MathUtils.random(x1,x2));
	}

	private float distance(float x1,float y1,float x2,float y2){
		float dist = (float)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y2-y1,2));
		return dist;
	}

	private float DPL(float x0, float y0, float x1, float y1, float x2, float y2){
		float d = Math.abs((y2-y1)*x0-(x2-x1)*y0+x2*y1-y2*x1)/sqrt(sq(y2-y1)+sq(x2-x1));
		return d;
	}

	private float getX(float val){  // get point (x or y)
		float x = val*cam.val+cam.xMid;
		return x;
	}

	private float getY(float val){  // get point (x or y)
		float x = val*cam.val+cam.yMid;
		return x;
	}

	private float unX(float val){  // get point (x or y)
		float x = (val-cam.xMid)/cam.val;
		return x;
	}

	private float unY(float val){  // get point (x or y)
		float x = (val-cam.yMid)/cam.val;
		return x;
	}

	private float unS(float val){  // get size
		return val/cam.val;
	}

	private float getS(float val){  // get size
		float v = val*cam.val;
		if (v<3){
			v = 3;
		}
		return v;
	}

	// rescale images when cam is zoomed
	private void rescale(){

	}

	private boolean offScreen(float x, float y, float r){
		boolean b = false;
		if (x+r<0||y+r<0||x-r>SCREENX||y-r>SCREENY){
			b = true;
		}
		return b;
	}

	class PP{
		float x, y;
		float r;
		float timer = 0;
		float timerMax = 10;
		int num;
		String tag;

		private void set(int n, float x, float y, String tag){
			this.r = 2*RBUT;
			this.x = x;
			this.y = y;
			this.num = n;
			this.tag = tag;
			if (n==0) {
				f1Img.setCenter(this.x, this.y);
			} else {
				f2Img.setCenter(this.x, this.y);
			}
			this.setImg();
		}
		private void setImg(){
			pp1.x = gun.x-gun.w*cos(gun.ang);
			pp1.y = gun.y-gun.w*sin(gun.ang);
			f1Img.setCenter(pp1.x,pp1.y);
		}
		private void hit(){
			this.timer = this.timerMax;
		}
		private void go() {
			if (this.timer > 0) {
				this.timer--;
			}
			float r = this.r + this.r / 10 * sin(-this.timer / this.timerMax * PI);
			float colP = this.timer/this.timerMax;
			if (this.num==0) {
				SB();
				circ(this.x, this.y, r, 0, colP, 1-colP, 0);
				SE();
				batch.begin();
				f1Img.setScale(r*1.5f / f1Img.getHeight());
				f1Img.draw(batch);
				layout.setText(font,this.tag);
				font.draw(batch,this.tag,this.x-layout.width/2,this.y+this.r+layout.height*2);
				batch.end();
			}
			SB();
			circ(this.x,this.y,r,4,.5f,.5f,.5f);
			SE();
			if (!touch&&Gdx.input.isTouched()){
				if (distance(mx,my,this.x,this.y)<this.r){
					this.hit();
					touch = true;
				}
			} else {
				this.timer = 0;
			}
		}
	}

	class Cam{
		float x[] = new float[2];
		float y[] = new float[2];
		int ind[] = new int[2];
		float dist[] = new float[2];
		boolean drag[] = new boolean[2];
		float min, max, val, startVal;
		float xMid, yMid, xStart, yStart;
		SliderBar scaleSlider;

		private void setInit(float min, float max, float val) {
			this.min = min;
			this.max = max;
			this.startVal = val;
			this.val = val;
			this.scaleSlider = new com.mygdx.spectrometer.spectrometer.SliderBar(SCREENX-1.5f*WSLIDER,WSLIDER/4,this.min,this.max,this.val,"scale",0,"",-1,false);
			this.reset();
		}

		private void reset(){
			this.val = this.startVal;
			this.xStart = MIDX;
			this.yStart = MIDY;
			this.xMid = this.xStart;
			this.yMid = this.yStart;
			this.clear();
		}

		private void clear(){
			this.x = new float[]{0, 0};
			this.y = new float[]{0, 0};
			this.ind = new int[]{-1,-1};
			this.dist = new float[] {0,0};
			this.drag = new boolean[] {false,false};
		}
		private void go(){

			// computer version with a scale slidervar
			if (!app) {
				this.scaleSlider.go();
				if (this.scaleSlider.move) {
					this.val = this.scaleSlider.val;
					rescale();
				}
			}

			if (!touch&&Gdx.input.justTouched()) {
				if (!this.drag[0]) {
					for (int i = 0; i < 20; i++) {
						if (Gdx.input.isTouched(i)&&mx>MIDX*.5f) {
							float gx = Gdx.input.getX(i);
							float gy = (viewport.getWorldHeight() - Gdx.input.getY(i));
							this.x[0] = gx;
							this.y[0] = gy;
							this.x[1] = gx;
							this.y[1] = gy;
							this.ind[0] = i;
							this.drag[0] = true;
							touch = true;
							break;
						}
					}
				}
			}

			if (this.drag[0]&&!this.drag[1]){
				for (int i = 0; i<20; i++){
					if (Gdx.input.isTouched(i)&&i!=this.ind[0]){
						this.ind[1] = i;
						this.drag[1] = true;
						float gx = Gdx.input.getX(i);
						float gy = (viewport.getWorldHeight() - Gdx.input.getY(i));
						this.x[1] = gx;
						this.y[1] = gy;
						this.dist[0] = distance(this.x[0],this.y[0],this.x[1],this.y[1]);
					}
				}
			}

			// dragging
			if (true) {
				if (this.drag[0] && !this.drag[1]) {
					this.x[1] = Gdx.input.getX(this.ind[0]);
					this.y[1] = (viewport.getWorldHeight() - Gdx.input.getY(this.ind[0]));
					this.xMid += this.x[1] - this.x[0];
					this.yMid += this.y[1] - this.y[0];
					this.x[0] = this.x[1];
					this.y[0] = this.y[1];
				}
			}
			// pinching
			if (this.drag[0]&&this.drag[1]){
				float gx1 = Gdx.input.getX(this.ind[0]);
				float gy1 = (viewport.getWorldHeight() - Gdx.input.getY(this.ind[0]));
				float gx2 = Gdx.input.getX(this.ind[1]);
				float gy2 = (viewport.getWorldHeight() - Gdx.input.getY(this.ind[1]));
				this.dist[1] = distance(gx1,gy1,gx2,gy2);
				this.val *= this.dist[1]/this.dist[0];
				this.val = limit(this.val,this.min,this.max);
				this.dist[0] = this.dist[1];
				rescale();
			}
			if (!Gdx.input.isTouched()){
				this.clear();
			}
		}
	}

	private void dash(float x1, float y1, float x2, float y2, float t, float lDash, float R, float G, float B){
		shapeRenderer.setColor(R,G,B,1);
		float ang = PI/2;
		float xa,ya,xb,yb;
		if (x1!=x2){
			ang = atan(y2-y1,x2-x1);
		}
		float d = distance(x1,y1,x2,y2);
		for (float i = 0; i<=d; i+=lDash*2){
			xa = x1+i*cos(ang);
			ya = y1+i*sin(ang);
			xb = xa+lDash*cos(ang);
			yb = ya+lDash*sin(ang);
			shapeRenderer.rectLine(xa,ya,xb,yb,t);
		}
	}

	private float DR(float val){
		return MathUtils.degRad*val;
	}
	private float RD(float val){
		return (float)(MathUtils.radDeg*val);
	}
	private void rectAng(float x, float y, float w, float h,float ang, float t, float R, float G, float B){
		float[] P1 = new float [] {x+w/2,y+h/2};
		float[] P2 = new float [] {x+w/2,y-h/2};
		float[] P3 = new float [] {x-w/2,y+h/2};
		float[] P4 = new float [] {x-w/2,y-h/2};
		P1 = rotate(P1[0],P1[1],x,y,ang);
		P2 = rotate(P2[0],P2[1],x,y,ang);
		P3 = rotate(P3[0],P3[1],x,y,ang);
		P4 = rotate(P4[0],P4[1],x,y,ang);
		shapeRenderer.setColor(R, G, B, 1);
		if (t==0) {
			shapeRenderer.triangle(P1[0], P1[1], P2[0], P2[1], P3[0], P3[1]);
			shapeRenderer.triangle(P2[0], P2[1], P3[0], P3[1], P4[0], P4[1]);
			shapeRenderer.triangle(P3[0], P3[1], P4[0], P4[1], P1[0], P1[1]);
			shapeRenderer.triangle(P4[0], P4[1], P1[0], P1[1], P2[0], P2[1]);
		} else{
			line(P1[0],P1[1],P2[0],P2[1],t,R,G,B);
			line(P1[0],P1[1],P3[0],P3[1],t,R,G,B);
			line(P4[0],P4[1],P3[0],P3[1],t,R,G,B);
			line(P4[0],P4[1],P2[0],P2[1],t,R,G,B);
		}
	}
	private void circ(float x, float y, float r, float t, float R, float G, float B){
		shapeRenderer.setColor(R,G,B,1);
		if (t == 0) {
			shapeRenderer.circle(x, y, r);
		} else {
			float x1, y1, x2, y2;
			int N = 72; // segments - one for every 5 degrees
			float addAng = DR(360F/(float)N);
			shapeRenderer.setColor(R,G,B,1);
			for (float i = 0; i<=2*PI; i+=addAng){
				x1 = (float)(x+r*Math.cos(i));
				y1 = (float)(y+r*Math.sin(i));
				x2 = (float)(x+r*Math.cos(i+addAng*1.2F));
				y2 = (float)(y+r*Math.sin(i+addAng*1.2F));
				shapeRenderer.rectLine(x1, y1, x2, y2, t);
			}
		}
	}

	private float[] sort(float[] list){
		float[] tempList = new float[list.length];
		float temp;
		for (int i = 0; i<list.length; i++){
			tempList[i] = list[i];
		}
		for (int i = 0; i<list.length; i++){
			for (int j = 1; j<list.length; j++){
				if (tempList[j]<tempList[j-1]){
					temp = tempList[j-1];
					tempList[j-1] = tempList[j];
					tempList[j] = temp;
				}
			}
		}
		return tempList;
	}

	private float[] rotate(float x, float y, float x0, float y0, float ang){
		float px = x-x0;
		float py = y-y0;
		float c = (float)(Math.cos(ang));
		float s = (float)(Math.sin(ang));
		float xNew = px*c-py*s;
		float yNew = px*s+py*c;
		float[] newP = new float[]{xNew+x0,yNew+y0};
		return newP;
	}

	private void tri(float x1, float y1, float x2, float y2, float x3, float y3,float t, float R, float G, float B){
		shapeRenderer.setColor(R,G,B,1);
		if (t==0){
			shapeRenderer.triangle(x1,y1,x2,y2,x3,y3);
		} else {
			line(x1,y1,x2,y2,t,R,G,B);
			line(x3,y3,x2,y2,t,R,G,B);
			line(x1,y1,x3,y3,t,R,G,B);
		}
	}

	private float atan(float y, float x){
		float ang;
		if (x==0){
			if (y>0){
				ang = (float)Math.PI/2;
			} else {
				ang = (float)-Math.PI/2;
			}
		} else {
			ang = (float)(Math.atan(y/x));
			if (x<0){
				ang+=Math.PI;
			}
		}
		return ang;
	}

	private float sq(float val){
		return val*val;
	}
	private float sqrt(float val){
		return (float)Math.sqrt(val);
	}
	private float f(double val){
		return (float)(val);
	}

	private void SB(){
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	}
	private void SE(){
		shapeRenderer.end();
	}

	// draws a line with circles on ends
	private void line(float x1, float y1, float x2, float y2, float t, float R, float G, float B){
		shapeRenderer.setColor(R,G,B,1);
		shapeRenderer.rectLine(x1,y1,x2,y2,t);
		shapeRenderer.circle(x1,y1,t/2);
		shapeRenderer.circle(x2,y2,t/2);
	}
	// line with no circles
	private void line2(float x1, float y1, float x2, float y2, float t, float R, float G, float B){
		shapeRenderer.setColor(R,G,B,1);
		shapeRenderer.rectLine(x1,y1,x2,y2,t);
	}

	private void rect(float x, float y, float w, float h, float t, float R, float G, float B){
		shapeRenderer.setColor(R,G,B,1);
		if (t==0) {
			shapeRenderer.rect(x, y, w, h);
		} else {
			shapeRenderer.rectLine(x-t/2,y,x+w+t/2,y,t);
			shapeRenderer.rectLine(x,y-t/2,x,y+h+t/2,t);
			shapeRenderer.rectLine(x-t/2,y+h,x+w+t/2,y+h,t);
			shapeRenderer.rectLine(x+w,y+h+t/2,x+w,y-t/2,t);
		}
	}

	// line with no circles
	private void line2C(float x1, float y1, float x2, float y2, float t, float R, float G, float B){
		x1 = getX(x1);
		x2 = getX(x2);
		y1 = getY(y1);
		y2 = getY(y2);
		t = getS(t);
		shapeRenderer.setColor(R,G,B,1);
		shapeRenderer.rectLine(x1,y1,x2,y2,t);
	}

	// draws a line with circles on ends
	private void lineC(float x1, float y1, float x2, float y2, float t, float R, float G, float B){
		shapeRenderer.setColor(R,G,B,1);
		shapeRenderer.rectLine(getX(x1),getY(y1),getX(x2),getY(y2),getS(t));
		shapeRenderer.circle(getX(x1),getY(y1),getS(t/2));
		shapeRenderer.circle(getX(x2),getY(y2),getS(t/2));
	}

	private void circC(float x, float y, float r, float t, float R, float G, float B){
		x = getX(x);
		y = getY(y);
		r = getS(r);
		if (t!=0) {
			t = getS(t);
		}
		shapeRenderer.setColor(R,G,B,1);
		if (t == 0) {
			shapeRenderer.circle(x, y, r);
		} else {
			float x1, y1, x2, y2;
			int N = 72; // segments - one for every 5 degrees
			float addAng = DR(360F/(float)N);
			shapeRenderer.setColor(R,G,B,1);
			for (float i = 0; i<=2*PI; i+=addAng){
				x1 = (float)(x+r*Math.cos(i));
				y1 = (float)(y+r*Math.sin(i));
				x2 = (float)(x+r*Math.cos(i+addAng*1.2F));
				y2 = (float)(y+r*Math.sin(i+addAng*1.2F));
				shapeRenderer.rectLine(x1, y1, x2, y2, t);
			}
		}
	}

	// formats val to sd sig digs in decimal
	private String SF(float val, int sd){
		String p = "%."+String.valueOf(sd) + "f";
		String s = String.format(p, val);
		return s;
	}

	class SwitchButton{
		float xMid,yMid,w,r,xPos,v, xGoal, timer, timerMax, RL, GL, BL, RF, GF, BF, RB, GB, BB;	// line color, fill color, background color
		int n, pos;
		String names[];
		String tag;
		private void setInit(String tag, float x, float y, String[] names){
			this.xMid = x;
			this.yMid = y;
			this.timer = 0;
			this.timerMax = 10;
			this.v = 5;
			this.r = RBUT*.65f;
			this.n = names.length;
			this.w = this.r*(this.n*2-1);
			this.names = names;
			this.pos = 0;
			this.tag = tag;
			this.xPos = this.xMid-this.w/2;
			this.RL = 0;
			this.GL = 0;
			this.BL = 0;
			this.RF = 1;
			this.GF = 1;
			this.BF = 1;
			this.RB = 0;
			this.GB = 0;
			this.BB = 0;
		}
		private void setColour(float RL, float GL, float BL, float RF, float GF, float BF, float FB, float GB, float BB){
			this.RL = RL;
			this.GL = GL;
			this.BL = BL;
			this.RF = RF;
			this.GF = GF;
			this.BF = BF;
			this.RB = RB;
			this.GB = GB;
			this.BB = BB;

		}
		private void draw(){
			SB();
			float x;
			rect(this.xMid-this.w/2-this.r,this.yMid-this.r,this.w+this.r*2,this.r*2,0,this.RB,this.GB,this.BB);
			for (int i = 0; i<this.n; i++){
				x = this.xMid-this.w/2+i*this.w/(this.n-1);
				if (this.pos==i) {
					this.xGoal = x;
				}
			}
			//float colP = this.timer/this.timerMax;
			rect(this.xPos-this.r,this.yMid-this.r,2*this.r,2*this.r,0,this.RF,this.GF,this.BF);
			rect(this.xPos-this.r,this.yMid-this.r,2*this.r,2*this.r,3,this.RL,this.GL,this.BL);

			SE();
			batch.begin();
			String s = this.names[this.pos];
			layout.setText(font,s);
			font.draw(batch,s,this.xMid-layout.width/2,this.yMid-layout.height*1);
			layout.setText(font,this.tag);
			font.draw(batch,this.tag,this.xMid-layout.width/2,this.yMid+layout.height*2.0f);
			batch.end();
		}
		private void setMode(){

		}
		private void go(){
			this.draw();
			if (this.xPos>this.xGoal){
				this.xPos-=this.v;
				if (this.xPos<this.xGoal){
					this.xPos = this.xGoal;
				}
			}
			if (this.xPos<this.xGoal){
				this.xPos+=this.v;
				if (this.xPos>this.xGoal){
					this.xPos = this.xGoal;
				}
			}

			if (this.timer>0){
				this.timer--;
			}
			if (!touch&&Gdx.input.justTouched()){
				if (mx>this.xMid-this.w/2*1.2f&&mx<this.xMid+this.w/2*1.2f&&my>this.yMid-this.r*2&&my<this.yMid+this.r*2){
					touch = true;
					this.pos++;
					this.timer = this.timerMax;
					if (this.pos==this.n){
						this.pos = 0;
					}
					this.setMode();
				}
			}
		}
	}
	SwitchButton SBT = new SwitchButton();

	class DropWin{
		float x, y, val, border;
		float RL, GL, BL, REF, GEF, BEF, RSF, GSF, BSF, RMF, GMF, BMF, RC, GC, BC;	// line, empty fill, selected fill, main fill (title), current (under title)
		float w = 0;
		float h = 0;
		String tag, curTag, item, position;
		String[] names = new String[10];
		float[] vals = new float[10];
		int numberItems, currentItem, num;
		boolean selected = false;
		private void setInit(float x, float y, String tag, String[] names, float[] vals, int num, String position){
			this.position = position;
			this.x = x;
			this.y = y;
			this.tag = tag;
			this.item = names[0];
			this.num = num;
			this.numberItems = vals.length;
			for (int i = 0; i<this.numberItems; i++){
				this.names[i] = names[i];
				this.vals[i] = vals[i];
			}
			this.currentItem = 0;
			this.setDimensions();
			this.setColour(0,0,0,.95f,.95f,.95f,.8f,.8f,.8f,.9f,.9f,1,1,1,1);
		}
		private void setColour(float RL, float GL, float BL, float REF, float GEF, float BEF, float RSF, float GSF, float BSF, float RMF, float GMF, float BMF, float RC, float GC, float BC){
			this.RL = RL;
			this.GL = GL;
			this.BL = BL;
			this.REF = REF;
			this.GEF = GEF;
			this.BEF = BEF;
			this.RSF = RSF;
			this.GSF = GSF;
			this.BSF = BSF;
			this.RMF = RMF;
			this.GMF = GMF;
			this.BMF = BMF;
			this.RC = RC;
			this.BC = BC;
			this.GC = GC;
		}

		private void setDimensions(){
			float maxH, maxW;
			layout.setText(winFont, tag);
			maxH = layout.height;
			maxW = layout.width;
			for (int i = 0; i < this.numberItems; i++){
				layout.setText(winFont,this.names[i]);
				float w = layout.width;
				float h = layout.height;
				if (w>maxW) {
					maxW = w;
				}
				if (h>maxH) {
					maxH = h;
				}
			}
			this.border = maxH;
			this.h = this.border*1.5f+maxH;
			this.w = this.border*2+maxW;
			this.y -= this.h;
			if (this.position=="TR"){
				this.y = SCREENY-2*this.h;
				this.x = SCREENX-this.w-2*this.border;
			}
		}

		private void draw(){
			SB();
			// name area
			rect(this.x,this.y,this.w,this.h,0,this.RMF,this.GMF,this.BMF);
			rect(this.x,this.y,this.w,this.h,5,this.RL,this.GL,this.BL);
			SE();
			// write name
			batch.begin();
			layout.setText(winFont,this.tag);
			float x = this.x+this.w/2-layout.width/2;
			float y = this.y+layout.height/2+this.h/2;
			winFont.draw(batch,this.tag,x,y);
			batch.end();
			// drawing options
			if (this.selected){
				for (int i = 0; i<this.numberItems; i++){
					float y2 = this.y-(i+0)*this.h;
					float y1 = this.y-(i+1)*this.h;
					// outline around box
					SB();
					rect(this.x,y1,this.w,this.h,5,this.RL,this.GL,this.BL);

					if (mx > this.x && mx<this.x+this.w && my>y1 && my<y2){
						rect(this.x,y1,this.w,this.h,0,this.RSF,this.GSF,this.BSF);
						this.currentItem = i;
						this.curTag = this.names[this.currentItem];
					} else {
						rect(this.x,y1,this.w,this.h,0,this.REF,this.GEF,this.BEF);
					}
					rect(this.x,y1,this.w,this.h,3,this.RL,this.GL,this.BL);
					SE();
					// writing item name
					batch.begin();
					layout.setText(winFont,this.names[i]);
					x = this.x+this.w/2-layout.width/2;
					y = y1+layout.height/2+this.h/2;
					winFont.draw(batch,this.names[i],x,y);
					batch.end();
				}
			} else {    // just current selection
				float y1 = this.y-(1)*this.h;
				SB();
				// current item
				rect(this.x+2,y1+2,this.w-4,this.h-4,0,this.RC,this.GC,this.BC);
				// black outline around box
				rect(this.x,y1,this.w,this.h,3,this.RL,this.GL,this.BL);
				SE();
				// writing item name
				batch.begin();
				layout.setText(winFont,this.names[this.currentItem]);
				x = this.x+this.w/2-layout.width/2;
				y = y1+layout.height/2+this.h/2;
				winFont.draw(batch,this.names[this.currentItem],x,y);
				batch.end();
			}
		}

		private void go(){
			this.curTag = this.names[this.currentItem];
			this.val = this.vals[this.currentItem];
			// checking if clicked
			if (!touch&&Gdx.input.justTouched()) {
				if (mx>this.x && mx < this.x+this.w && my>this.y && my<this.y+this.h) {
					this.selected = true;
					touch = true;
				}
			}
			if (!Gdx.input.isTouched()){
				if (this.selected) {
					this.selected = false;
					this.item = this.curTag;
					setMode(this.curTag);
				}
			}
			this.draw();
		}
	}
	DropWin DR = new DropWin();

	class Button {
		float x, y, y0, RL, GL, BL, RF, GF, BF, REF, GEF, BEF, t;	// line, fill, empty fill
		int r = (int)RBUT;
		boolean b, canSwype;
		String tag;
		int num;

		public Button(float x, float y, boolean b, String tag, int num, boolean canSwype) {
			this.x = x;
			this.y = y;
			this.t = 3;
			this.y0 = this.y;
			this.tag = tag;
			this.b = b;
			this.num = num;
			this.canSwype = canSwype;
			this.r = 9;
			this.setColour(0,0,0,1,1,1, 1, 1, 1);
		}
		private void setColour(float RL, float GL, float BL, float RF, float GF, float BF, float REF, float GEF, float BEF){
			this.RL = RL;
			this.GL = GL;
			this.BL = BL;
			this.RF = RF;
			this.GF = GF;
			this.BF = BF;
			this.REF = REF;
			this.GEF = GEF;
			this.BEF = BEF;
		}

		public void go() {
			if (this.canSwype) {
				if (bswype.down) {
					this.y0 -= bswype.v;
					if (this.y0 < -2*RBUT) {
						this.y0 = -2*RBUT;
					}
				}
				if (bswype.up) {
					this.y0 += bswype.v;
					if (this.y0 > this.y) {
						this.y0 = this.y;
					}
				}
			}
			float y = this.y0;

			SB();
			float t = this.t;
			if (this.b) {
				// fill
				rect(this.x-this.r-t,this.y0-this.r-t,this.r*2+2*t,this.r*2+2*t,0,this.RF,this.GF,this.BF);
				SE();
				batch.begin();
				check.setCenter(this.x,this.y0);
				check.draw(batch);
				batch.end();
				SB();
			} else {
				rect(this.x-this.r-t,this.y0-this.r-t,this.r*2+2*t,this.r*2+2*t,0,this.REF,this.GEF,this.BEF);
			}
			// outline
			rect(this.x-this.r-2,this.y0-this.r-2,this.r*2+4,this.r*2+4,t,this.RL,this.GL,this.BL);
			SE();

			// name
			batch.begin();
			layout.setText(font, this.tag);
			float yT = y + layout.height / 2F;
			font.draw(batch, this.tag, this.x + 2*this.r+5, yT);
			batch.end();

			if (Gdx.input.justTouched()){
				if (mx > this.x - this.r*2 && mx < this.x + layout.width + this.r*2 && my > this.y0 - this.r*1.5f && my < this.y0 + this.r*1.5f) {
					this.b = !this.b;
					touch = true;
				}
			}
		}
	}


	class BSwype{
		boolean up = true;
		boolean down = false;
		float timer = 0;
		float v = 50;
		float x, y;
		private void setInit(){
			this.x = SCREENX-2*RBUT;
			this.y = 2*RBUT;
		}
		private void go(){
			if (Gdx.input.justTouched()&&!touch){
				if (distance(mx,my,this.x,this.y)<2*RBUT){
					this.up = !this.up;
					this.down = !this.down;
					touch = true;
				}
			}
			SB();
			this.timer += DR(5);
			float t = SCREENY/60;
			float colP = .5f + .5f * sin(this.timer);
			float x = this.x;
			float y = this.y-10*abs(sin(this.timer));
			float w = RBUT;
			float sign;
			if (this.down) {
				sign = -1;
			} else {
				sign = 1;
			}
			for (int i = 0; i<2; i+=1) {
				line(x - w, y + sign*w / 2 + i*w, x, y+i*w, t, colP, 0, 1 - colP);
				line(x + w, y + sign*w / 2 + i*w, x, y+i*w, t, colP, 0, 1 - colP);
			}
			SE();
		}
	}

	// set your buttons here
	// Button B1;...

	// central slider to see it easier
	class BigSlider{
		float w, x, y;
		float h = 72;
		String tag;
		float min, max, val, pos, snap;
		boolean b = false;
		boolean show, display;
		int SD;
		private void set(String tag, float min, float max, float val, float snap,boolean show, boolean display, int SD){
			this.w = WSLIDER*2;
			this.x = MIDX-WSLIDER;
			this.y = MIDY;
			this.tag = tag;
			this.min = min;
			this.max = max;
			this.val = val;
			this.snap = snap;
			this.show = show;
			this.display = display;
			this.SD = SD;
			this.b = true;
			this.setVal(val);
		}
		public void setVal(float val) {
			this.val = val;
			this.pos = ((this.val - this.min) / (this.max - this.min) * this.w + this.x);
			if (this.pos > this.x + this.w) {
				this.pos = this.x + this.w;
			}
			if (this.pos<this.x){
				this.pos = this.x;
			}
		}

		public void go() {
			if (!Gdx.input.isTouched()){
				this.b = false;
			}
			// drawing
			SB();
			line(this.x, this.y, this.x + this.w, this.y, this.h,.7F,.7F,.7F);
			circ(this.x, this.y, this.h / 2,0,.7F,.7F,.7F);
			circ(this.x + this.w, this.y, this.h / 2,0,0.7F,.7F,.7F);

			float colP = 0;
			circ(this.x,this.y,this.h/4,0,(1-colP)/2,colP,1-colP);

			for (int i = (int) this.x; i < this.pos; i++) {
				colP = (i - this.x) / (this.pos - this.x + 1);
				line(i,this.y,i+1,this.y,this.h/2,(1-colP)/2,colP,1-colP);
			}
			circ(this.pos,y,this.h/2.1f,this.h/5,.5F,.5F,.5F);
			SE();
			// name
			batch.begin();
			layout.setText(fontBig, this.tag);
			float x, y;
			if (false){//(this.tag.length() < 3) {    // harmonic labels to left
				x = this.x - layout.width - this.h;
				y = this.y + layout.height / 2;
			} else {                    // word labels above
				x = this.x + this.w / 2 - layout.width / 2;
				y = this.y + layout.height + this.h*.75f;

			}
			fontBig.draw(batch, this.tag, x, y);
			// value to the right
			if (this.show) {
				if (this.display) {
					String val = SF(this.val, this.SD);
					layout.setText(fontBig, val);
					fontBig.draw(batch, val, this.x + this.w/2-layout.width/2, this.y - this.h/1.5f);
				}
			} else { // hide for experiment
				layout.setText(fontBig, "?");
				fontBig.draw(batch, "?", this.x + this.w + 15, this.y + layout.height / 2);
			}
			batch.end();
		}
	}

	private float[] colourBlend(float[] col1, float[] col2, float percent){
		float diffR = col2[0]-col1[0];
		float diffG = col2[1]-col1[1];
		float diffB = col2[2]-col1[2];
		float R = percent*diffR;
		float G = percent*diffG;
		float B = percent*diffB;
		float[] colOut = {col1[0]+R,col1[1]+G,col1[2]+B};
		return colOut;
	}

	class SliderBar {
		float x, y, pos, min, max, x0, R, G, B, R2, G2, B2, RB, GB, BB;
		boolean move = false;
		boolean display;
		int SD;
		float w = WSLIDER;
		float h = WSLIDER/6;
		float val, snap;
		String tag, unit;
		boolean show = true; 	// whether to keep value a mystery for experiment
		boolean canSwype = true;
		String nameDir, valDir;

		public SliderBar(float x, float y, float min, float max, float val, String tag, float snap, String unit, int SD,boolean display) {
			this.SD = SD; 	// decimals to display
			this.display = display; 	// display the value
			this.x = x;
			this.x0 = x;
			this.y = y;
			this.min = min;
			this.max = max;
			this.val = val;
			this.tag = tag;
			this.snap = snap;
			this.unit = unit;
			this.nameDir = "top";
			this.valDir = "right";
			this.setColour(1,1,0,1,0,0,.75f,.75f,.75f);
		}
		private void setColour(float r, float g, float b, float r2, float g2, float b2, float rb, float gb, float bb){
			this.R = r;
			this.G = g;
			this.B = b;
			this.R2 = r2;
			this.G2 = g2;
			this.B2 = b2;
			this.RB = rb;
			this.GB = gb;
			this.BB = bb;
		}

		public void setVal(float val) {
			this.val = val;
		}

		public void go() {
			// drawing
			if (this.canSwype) {
				if (bswype.down) {
					this.x0 -= bswype.v;
					if (this.x0 < -2*WSLIDER) {
						this.x0 = -2*WSLIDER;
					}
				}
				if (bswype.up) {
					this.x0 += bswype.v;
					if (this.x0 > this.x) {
						this.x0 = this.x;
					}
				}
			}
			float x = this.x0;
			float xP = ((this.val - this.min) / (this.max - this.min) * this.w + x);
			if (this.val>this.max){
				xP = x+this.w;
			}
			if (this.val<this.min){
				xP = x;
			}


			SB();
			// background line
			line2(x,this.y,x+this.w,this.y,10,this.RB,this.GB,this.BB);
			if (this.R2==this.R&&this.G2==this.G&&this.B2==this.B) {
				line2(x, this.y, xP, this.y, 10, this.R, this.G, this.B);
			} else {
				for (float xl = 0; xl<xP-this.x; xl++){
					float col[] = colourBlend(new float[] {this.R,this.G,this.B}, new float[] {this.R2, this.G2, this.B2},xl/this.w);
					line2(xl+x,this.y,xl+x+1,this.y,10,col[0],col[1],col[2]);
				}

			}
			if (this.val!=this.min) {
				float col[] = colourBlend(new float[] {this.R,this.G,this.B}, new float[] {this.R2, this.G2, this.B2},(xP-this.x)/(this.w));//(xP-this.x));
				line2(xP, this.y - 7, xP, this.y + 7, 6, col[0],col[1],col[2]);
			}
			SE();
			// name
			batch.begin();
			layout.setText(font, this.tag);
			float xL = 0;
			float y = 0;
			// word labels
			if (this.nameDir=="left"){
				xL = x - layout.width - 10;
				y = this.y + layout.height / 2;
			} else if (this.nameDir=="top"){
				xL = x + this.w / 2 - layout.width / 2;
				y = this.y + this.h/2+layout.height*1f+5;
			} else if (this.nameDir=="bottom"){
				xL = x + this.w / 2 - layout.width / 2;
				y = this.y - this.h/2+layout.height*0f;
			} else if (this.nameDir=="right"){
				xL = x + this.w + 10;
				y = this.y + layout.height/2;
			}
			font.draw(batch, this.tag, xL, y);
			// value
			if (this.show) {
				if (this.display) {
					String val = SF(this.val, this.SD);
					layout.setText(font, val);
					if (this.valDir=="left"){
						xL = x - layout.width - 10;
						y = this.y + layout.height / 2;
					} else if (this.valDir=="top"){
						xL = x + this.w / 2 - layout.width / 2;
						y = this.y + this.h/2+layout.height*1f+5;
					} else if (this.valDir=="bottom"){
						xL = x + this.w / 2 - layout.width / 2;
						y = this.y - this.h/2+layout.height*0f;
					} else if (this.valDir=="right"){
						xL = x + this.w + 10;
						y = this.y + layout.height/2;
					}
					font.draw(batch, val, xL,y);
				}
			} else { // hide for experiment
				layout.setText(font, "?");
				font.draw(batch, "?", x + this.w + 15, this.y + layout.height / 2);
			}
			batch.end();

			// checking if clicked
			if (Gdx.input.justTouched()&&!touch){
				if (mx > x - this.h && mx < x + this.w + this.h && my > this.y - this.h && my < this.y + this.h) {
					this.move = true;
					touch = true;
				}
			} else if (!Gdx.input.isTouched()) {
				this.move = false;
			}
			if (this.move) {
				this.pos = mx;
				if (this.pos < this.x) {
					this.pos = this.x;
				} else if (this.pos > this.x + this.w) {
					this.pos = this.x + this.w;
				}
				// setting value
				this.val = (float) (this.pos - this.x) / this.w * (this.max - this.min) + this.min;
				// snapping value
				if (this.snap != 0) {
					this.val = Math.round(this.val / this.snap) * this.snap;
					this.setVal(this.val);
				}

				float val = this.val;
				BS.set(this.tag,this.min,this.max,val,this.snap,this.show,this.display,this.SD);
			}
		}
	}

	class Reset{
		float x,y,size;
		float timer = 0;
		float timerMax = 10;
		private void setInit(String L, float size){
			this.size = size;
			if (L == "TR") {
				this.x = SCREENX-this.size;
				this.y = SCREENY-this.size;
			} else if (L == "BR"){
				this.x = SCREENX-this.size;
				this.y = this.size;
			} else if (L =="BL"){
				this.x = this.size;
				this.y = this.size;
			} else if (L == "TL"){
				this.x = this.size;
				this.y = SCREENY-this.size;
			} else { // custom location
				this.x = bswype.x - this.size;
				this.y = bswype.y;
			}
		}
		private void go(){
			batch.begin();
			resetImg.setCenter(this.x,this.y);
			resetImg.setScale((this.size-.2f*this.size*sin(PI*this.timer/this.timerMax))/resetImg.getHeight());
			//float colP = this.timer/this.timerMax;
			//resetImg.setColor(1-colP,colP,colP,1);
			resetImg.draw(batch);
			batch.end();
			if (Gdx.input.justTouched()&&!touch) {
				if (distance(mx,my,this.x,this.y)<this.size*.7f){
					this.timer = this.timerMax;
					touch = true;
					resetAll();
				}
			}
			if (this.timer>0){
				this.timer--;
			}
		}
	}
	Reset reset = new Reset();

	// makes sure a variable is within a range or sets it to min or max
	private float limit(float val, float min, float max){
		if (val<min){
			val = min;
		}
		if (val>max){
			val = max;
		}
		return val;
	}

	private void arrow(float x1, float y1, float x2, float y2, float t, float R, float G, float B) {
		float a = atan(y2-y1,x2-x1)+PI;
		if (x2==x1){
			if (y2<y1) {
				a = PI / 2;
			} else {
				a = -PI/2;
			}
		}
		float vLen = distance(x1, y1, x2, y2) / 6f;
		if (vLen>0) {
			vLen = limit(vLen, 10, 25);
		}
		// line version
		if (true) {
			line(x1,y1,x2,y2,t,R,G,B);
			float xe1 = x2 + vLen * cos(a + PI / 6);
			float ye1 = y2 + vLen * sin(a + PI / 6);
			line(x2, y2, xe1, ye1, t * .85f, R, G, B);
			xe1 = x2 + vLen * cos(a - PI / 6);
			ye1 = y2 + vLen * sin(a - PI / 6);
			line(x2, y2, xe1, ye1, t * .85f, R, G, B);
			circ(x2, y2, t / 2, 0, R, G, B);
		}
	}

	private BitmapFont font, creditFont;
	Texture bb1,bb2,bb3,bb4,bb5,img;
	Sprite resetImg, check;


	private void clearScreen() {
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private void resetAll(){

	}

	float ratio = 1;
	@Override
	public void create () {
		// computer mode
		if (!app) {
			Gdx.graphics.setWindowedMode(1280,640);
			Gdx.graphics.setTitle("Mass Spectrometer");
			// 2094 x 1080 samsung s8 --> (1.94 scale)
			// 1920 x 1080 is 1080 p
			// 1280 x 720 is 720 p
			// 1280 x 640 keeps close to aspect ratio of phone
		}
		SCREENX = Gdx.graphics.getWidth();
		SCREENY = Gdx.graphics.getHeight();
		ratio = SCREENX/2094f;

		MIDX = SCREENX / 2;
		MIDY = SCREENY / 2;
		RBUT = SCREENX/60;
		WSLIDER = SCREENX/10;
		if (!app){
			RBUT*=.65f;
			WSLIDER*=.8f;
		}
		BUTX = 2*RBUT;
		SLIDERX = RBUT+2;
		BUTY = SCREENY-2*RBUT;
		PI = (float)Math.PI;
		TD = SCREENX/30;
		// set all buttons, sliders, objects here...
		//bswype.setInit();
		//rev.setInit();
		//cam.setInit(.25f,2,1);
		//reset.setInit("TR",30);
		//SBT.setInit("mode",MIDX,SCREENY-3*RBUT,new String[] {"normal","C-14","velocity selector"});
		//DR.setInit(MIDX,MIDY,"Title",new String[] {"box 1","box 2","box 3"}, new float[] {1,2,3},0,"TR");


		camera = new OrthographicCamera(SCREENX,SCREENY);
		camera.position.set(SCREENX/2, SCREENY/2,0);
		camera.update();
		viewport = new FitViewport(SCREENX,SCREENY,camera);
		font = new BitmapFont(Gdx.files.internal("font2.fnt"));
		if (!app){
			font = new BitmapFont(Gdx.files.internal("font.fnt"));
		}
		fontBig = new BitmapFont(Gdx.files.internal("font.fnt"));
		creditFont = new BitmapFont(Gdx.files.internal("creditFont.fnt"));
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		creditFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		check = new Sprite(new Texture("check.png"));
		check.setScale(RBUT*2/check.getHeight());
		resetImg = new Sprite(new Texture("reset.png");

		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();

	}
	private void credit(){
		batch.begin();
		String tagLine = "2018 mattcraig.org";
		layout.setText(creditFont,tagLine);
		float x = 20;//MIDX-layout.width/2;
		float y = layout.height+15;
		creditFont.draw(batch,tagLine,x,y);
		batch.end();
	}
	// revert window size to 1280 x 640
	class Revert{
		float x, y, r;class
		private void setInit(){
			this.r = 5;
			this.x = 2*this.r;
			this.y = 2*this.r;
		}
		private void go(){
			int w = Gdx.graphics.getWidth();
			int h = Gdx.graphics.getHeight();
			if (!(w==1280&&h==640)){
				SB();
				rect(this.x-this.r,this.y-this.r,2*this.r,2*this.r,0,0,0,0);
				SE();
				if (!touch&&Gdx.input.justTouched()){
					if (distance(mx,my,this.x,this.y)<=this.r){
						resize(1280,640);
						touch = true;
					}
				}
			}
		}
	}
	Revert rev = new Revert();
	// computer mode
	public void resize(int width, int height) {
		if (!app) {
			// keep window size ratio of 1280 x 800
			if (f(width) / f(height) > SCREENX / SCREENY) {
				width = (int) (height * SCREENX / f(SCREENY));
			} else {
				height = (int) (width * SCREENY / f(SCREENX));
			}
			Gdx.graphics.setWindowedMode(width, height);
			viewport.update(width, height);
			batch.setProjectionMatrix(camera.projection);
			batch.setTransformMatrix(camera.view);
		}
	}


	@Override
	public void render () {
		// computer mode
		if (!app) {
			float gx = Gdx.graphics.getWidth();
			float gy = Gdx.graphics.getHeight();
			float yDiff = SCREENY-gy;
			float yScale = SCREENY/gy;
			float xScale = SCREENX/gx;
			mx = (Gdx.input.getX())*xScale;
			my = (viewport.getWorldHeight() - Gdx.input.getY() - yDiff)*yScale;
		} else {
			mx = (Gdx.input.getX());
			my = (viewport.getWorldHeight() - Gdx.input.getY());
		}

		clearScreen();
		// main code here
		//bswype.go();
		//reset.go();

		if (BS.b&&app){
			BS.go();
		}

		//cam.go();
		//rev.go();
		if (!Gdx.input.isTouched()){
			touch = false;
		}
		if (!app){
			credit();
		}
	}




	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
