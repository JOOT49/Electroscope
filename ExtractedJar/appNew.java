package com.mygdx.forces; // change this name

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


public class forces extends ApplicationAdapter {

	boolean app = true;

	SpriteBatch batch;
	private GlyphLayout layout = new GlyphLayout();
	private Viewport viewport;
	private Camera camera;
	private ShapeRenderer shapeRenderer;
	private int SCREENX;
	private int SCREENY;
	float MIDX, MIDY, BUTX, BUTY, PI, WSLIDER, RBUT, mx, my, TD;
	boolean touch = false;

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
			this.scaleSlider = new SliderBar(SCREENX-1.5f*WSLIDER,WSLIDER/4,this.min,this.max,this.val,"scale",0,"",-1,false);
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
						if (Gdx.input.isTouched(i)) {
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
	Cam cam = new Cam();

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
			ang = 90;
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
			shapeRenderer.rectLine(x,y,x+w,y,t);
			shapeRenderer.rectLine(x,y,x,y+h,t);
			shapeRenderer.rectLine(x,y+h,x+w,y+h,t);
			shapeRenderer.rectLine(x+w,y+h,x+w,y,t);
			shapeRenderer.circle(x,y,t/2);
			shapeRenderer.circle(x+w,y,t/2);
			shapeRenderer.circle(x,y+h,t/2);
			shapeRenderer.circle(x+w,y+h,t/2);
		}
	}

	// formats val to sd sig digs in decimal
	private String SF(float val, int sd){
		String p = "%."+String.valueOf(sd) + "f";
		String s = String.format(p, val);
		return s;
	}

	class Button {
		float x, y, y0;
		int r = (int)RBUT;
		boolean b, canSwype;
		String tag;
		int num;

		public Button(float x, float y, boolean b, String tag, int num, boolean canSwype) {
			this.x = x;
			this.y = y;
			this.y0 = this.y;
			this.tag = tag;
			this.b = b;
			this.num = num;
			this.canSwype = canSwype;
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
			if (true) {
				batch.begin();
				if (!this.b) {
					off.setScale(this.r*2 / off.getHeight());
					off.setCenter(this.x, y);
					off.draw(batch);
				} else {
					on.setScale(this.r*2 * 5 / 4 / on.getHeight());
					on.setCenter(this.x, y);
					on.draw(batch);
				}
				batch.end();
				SB();
				circ(this.x,y,this.r/2*2,this.r/4,.5F,.5F,.5F);
				SE();
			} else {
				SB();
				if (!this.b) {
					circ(this.x, y, this.r ,0, 0, 0, 0);
				} else {
					float colP;
					for (float i = this.r; i>=0; i--){
						colP = 1-sq(.5f*i/r);
						circ(x,y,i,0,0,colP*1,0);
					}
				}
				circ(this.x,y,this.r,5,0,0,0);
				SE();
			}


			// name
			batch.begin();
			layout.setText(font, this.tag);
			float yT = y + layout.height / 2F;
			font.draw(batch, this.tag, this.x + this.r+20, yT);
			batch.end();

			if (Gdx.input.justTouched()){
				if (mx > this.x - this.r && mx < this.x + layout.width + this.r && my > this.y0 - this.r && my < this.y0 + this.r) {
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
	BSwype bswype = new BSwype();

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
			SE();
			batch.begin();

			if (this.val==this.min){
				minS.setScale(this.h / minS.getHeight());
				minS.setCenterX(this.pos);
				minS.setCenterY(this.y);
				minS.draw(batch);
			} else if (this.val==this.max){
				maxS.setScale(this.h / maxS.getHeight());
				maxS.setCenterX(this.pos);
				maxS.setCenterY(this.y);
				maxS.draw(batch);
			} else if (true){//
				on.setScale(this.h / on.getHeight());
				on.setCenterX(this.pos);
				on.setCenterY(this.y);
				on.draw(batch);
			}
			batch.end();
			SB();
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

	BigSlider BS = new BigSlider();

	class SliderBar {
		float x, y, pos, min, max;
		boolean move = false;
		boolean display;
		int SD;
		float w = WSLIDER;
		float h = WSLIDER/5;
		float val, snap;
		String tag, unit;
		boolean show = true; 	// whether to keep value a mystery for experiment

		public SliderBar(float x, float y, float min, float max, float val, String tag, float snap, String unit, int SD,boolean display) {
			this.SD = SD; 	// decimals to display
			this.display = display; 	// display the value
			this.x = x;
			this.y = y;
			this.min = min;
			this.max = max;
			this.val = val;
			this.pos = ((this.val - this.min) / (this.max - this.min) * this.w + this.x);
			this.tag = tag;
			this.snap = snap;
			this.unit = unit;
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
			SE();
			batch.begin();

			if (this.val==this.min){
				minS.setScale(this.h / minS.getHeight());
				minS.setCenterX(this.pos);
				minS.setCenterY(this.y);
				minS.draw(batch);
			} else if (this.val==this.max){
				maxS.setScale(this.h / maxS.getHeight());
				maxS.setCenterX(this.pos);
				maxS.setCenterY(this.y);
				maxS.draw(batch);
			} else if (true){//(!this.move) {
				on.setScale(this.h / on.getHeight());
				on.setCenterX(this.pos);
				on.setCenterY(this.y);
				on.draw(batch);
			}
			batch.end();
			SB();
			circ(this.pos,y,this.h/2.1f,this.h/4,.5F,.5F,.5F);
			SE();
			// name
			batch.begin();
			layout.setText(font, this.tag);
			float x, y;
			if (false){//(this.tag.length() < 3) {    // harmonic labels to left
				x = this.x - layout.width - this.h;
				y = this.y + layout.height / 2;
			} else {                    // word labels above
				x = this.x + this.w / 2 - layout.width / 2;
				y = this.y + this.h/2 + layout.height*1.5f;
			}
			font.draw(batch, this.tag, x, y);
			// value to the right
			if (this.show) {
				if (this.display) {
					String val = SF(this.val, this.SD);
					layout.setText(font, val);
					font.draw(batch, val, this.x + this.w + this.h, this.y + layout.height / 2);
				}
			} else { // hide for experiment
				layout.setText(font, "?");
				font.draw(batch, "?", this.x + this.w + 15, this.y + layout.height / 2);
			}
			batch.end();

			// checking if clicked
			if (Gdx.input.justTouched()&&!touch){
				if (mx > this.x - this.h && mx < this.x + this.w + this.h && my > this.y - this.h && my < this.y + this.h) {
					this.move = true;
					touch = true;
				}
			} else if (!Gdx.input.isTouched()) {
				this.move = false;
			}
			float oldVal = this.val;
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
				BS.set(this.tag,this.min,this.max,this.val,this.snap,this.show,this.display,this.SD);
			}
		}
	}

	class Reset{
		float x,y,size;
		float timer = 0;
		float timerMax = 10;
		private void setInit(){
			this.size = SCREENX*.03f;
			this.x = SCREENX*.97f;
			this.y = SCREENY-.03f*SCREENX;
		}
		private void go(){
			batch.begin();
			resetImg.setCenter(this.x,this.y);
			resetImg.setScale((this.size-.2f*this.size*sin(PI*this.timer/this.timerMax))/resetImg.getHeight());
			float colP = this.timer/this.timerMax;
			resetImg.setColor(1-colP,colP,colP,1);
			resetImg.draw(batch);
			batch.end();
			if (Gdx.input.justTouched()) {
				if (distance(mx,my,this.x,this.y)<this.size*.7f){
					this.timer = this.timerMax;
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
		float vLen = distance(x1, y1, x2, y2) / 8f;
		// line version
		if (false) {
			line(x1,y1,x2,y2,t,R,G,B);
			float xe1 = x2 + vLen * cos(a + PI / 6);
			float ye1 = y2 + vLen * sin(a + PI / 6);
			line(x2, y2, xe1, ye1, t * .85f, R, G, B);
			xe1 = x2 + vLen * cos(a - PI / 6);
			ye1 = y2 + vLen * sin(a - PI / 6);
			line(x2, y2, xe1, ye1, t * .85f, R, G, B);
			circ(x2, y2, t / 2, 0, R, G, B);
		}
		// triangle version
		float thick = t;
		if (t==-1){
			thick = abs(y2-y1)/10;
		}

		line2(x1,y1,x2,y1+.6f*(y2-y1),thick,R,G,B);
		tri(x2,y2,x2-1.75f*vLen,y1+.6f*(y2-y1),x2+1.75f*vLen,y1+.6f*(y2-y1),0,R,G,B);
	}

	private BitmapFont font, creditFont, fontBig, fontSmall;
	Texture bb1,bb2,bb3,bb4,bb5,img;
	Sprite on,off,orange,minS,maxS,resetImg;



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
			Gdx.graphics.setTitle("Animal Cell");
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
			RBUT*=.8f;
			WSLIDER*=.8f;
		}
		BUTX = RBUT+20;
		BUTY = SCREENY-60;
		PI = (float)Math.PI;
		TD = SCREENX/30;
		// set all buttons, sliders, objects here...
		bswype.setInit();
		cam.setInit(.25f,2,1);
		reset.setInit();


		camera = new OrthographicCamera(SCREENX,SCREENY);
		camera.position.set(SCREENX/2, SCREENY/2,0);
		camera.update();
		viewport = new FitViewport(SCREENX,SCREENY,camera);
		font = new BitmapFont(Gdx.files.internal("font2.fnt"));
		if (!app){
			font = new BitmapFont(Gdx.files.internal("fontSmall.fnt"));
		}
		fontBig = new BitmapFont(Gdx.files.internal("font.fnt"));
		creditFont = new BitmapFont(Gdx.files.internal("creditFont.fnt"));
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		creditFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);


		img = new Texture("reset.png");
		resetImg = new Sprite(img);
		bb1 = new Texture("on.png");
		bb2 = new Texture("off.png");
		bb3 = new Texture("orange.png");
		bb4 = new Texture("min.png");
		bb5 = new Texture("max.png");
		on = new Sprite(bb1);
		off = new Sprite(bb2);
		orange = new Sprite(bb3);
		minS = new Sprite(bb4);
		maxS = new Sprite(bb5);
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();

	}

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




		if (BS.b&&app){
			BS.go();
		}

		//cam.go();
		if (!Gdx.input.isTouched()){
			touch = false;
		}
	}




	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
