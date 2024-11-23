package com.mygdx.wave1d; // change this name

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

import static java.lang.Math.PI;
import static java.lang.Math.abs;


public class MyGdxGamewave1d extends ApplicationAdapter {
	SpriteBatch batch;
	private GlyphLayout layout = new GlyphLayout();
	private Viewport viewport;
	private Camera camera;
	boolean click = false;
	private ShapeRenderer shapeRenderer;
	private int SCREENX;
	private int SCREENY;
	float MIDX, MIDY, BUTX, BUTY, PI, TD, FPS;

	boolean pause = true;

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

	private float f(double val){
		return (float)val;
	}

	// rotations
	private float[] rotate(float x, float y, float x0, float y0, float ang){
		float px = x-x0;
		float py = y-y0;
		float c = (float)(Math.cos(ang));
		float s = (float)(Math.sin(ang));
		float xNew = px*c-py*s;
		float yNew = px*s+py*c;
		float[] P = new float[] {xNew+x0,yNew+y0};
		return P;
	}

	private float distance(float x1,float y1,float x2,float y2){
		float dist = (float)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y2-y1,2));
		return dist;
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

	private void resetAll() {

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

	private void SB(){
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	}
	private void SE(){
		shapeRenderer.end();
	}

	// draws a line with filled
	private void line(float x1, float y1, float x2, float y2, float t, float R, float G, float B){
		shapeRenderer.setColor(R,G,B,1);
		shapeRenderer.rectLine(x1,y1,x2,y2,t);
		shapeRenderer.circle(x1,y1,t/2);
		shapeRenderer.circle(x2,y2,t/2);
	}

	// draws a line with filled without circles on ends
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

	// draws an arc of a circle
	private void arc(float x, float y, float r, float start, float stop, float thick, float R, float G, float B){
		float addAng = MathUtils.degRad*5;
		float x1, y1, x2, y2,j;
		shapeRenderer.setColor(R,G,B,1);
		for (float i = start; i<=stop; i+=addAng){
			j = i+addAng*1.2F;
			if (j>stop){
				j = stop;
			}
			x1 = (float)(x+r*Math.cos(i));
			y1 = (float)(y+r*Math.sin(i));
			x2 = (float)(x+r*Math.cos(j));
			y2 = (float)(y+r*Math.sin(j));
			shapeRenderer.rectLine(x1,y1,x2,y2,thick);
		}
	}

	// draws an ellipse using filled
	private void ellipse(float x, float y, float a, float b, float thick, float R, float G, float B){
		float x1, y1, x2, y2;
		int N = 30; // segments
		float addAng = (float)(2*Math.PI/N);
		shapeRenderer.setColor(R,G,B,1);
		for (float i = 0; i<2*Math.PI; i+=addAng){
			x1 = (float)(x+a*Math.cos(i));
			y1 = (float)(y+b*Math.sin(i));
			x2 = (float)(x+a*Math.cos(i+addAng*1.2F));
			y2 = (float)(y+b*Math.sin(i+addAng*1.2F));
			shapeRenderer.rectLine(x1,y1,x2,y2,thick);
		}
	}



	float RBUT;
	class Button {
		float x, y, y0;
		int r = (int)RBUT;
		boolean b;
		String tag;
		int num;
		boolean canSwype;

		public Button(float x, float y, boolean b, String tag, int num, boolean canSwype) {
			this.x = x;
			this.y = y;
			this.y0 = y;
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

			if (Gdx.input.justTouched()&&!click){
				if (mx > this.x - this.r && mx < this.x + layout.width + this.r && my > y - this.r && my < y + this.r) {
					this.b = !this.b;
					click = true;

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
			if (Gdx.input.justTouched()){
				if (distance(mx,my,this.x,this.y)<2*RBUT){
					this.up = !this.up;
					this.down = !this.down;
				}
			}
			SB();
			this.timer += DR(5);
			float t = SCREENX/100;
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
			this.x = MIDX-w/2;
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
			} else {
				on.setScale(this.h / on.getHeight());
				on.setCenterX(this.pos);
				on.setCenterY(this.y);
				on.draw(batch);
			}
			batch.end();
			SB();
			circ(this.pos,y,this.h/2.1f,8,.5F,.5F,.5F);
			SE();
			// name
			batch.begin();
			layout.setText(fontBig, this.tag);
			float x, y;
			if (this.tag.length() < 3) {    // harmonic labels to left
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

	float WSLIDER = 200;
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
			} else {
				on.setScale(this.h / on.getHeight());
				on.setCenterX(this.pos);
				on.setCenterY(this.y);
				on.draw(batch);
			}
			SB();
			circ(this.pos,y,this.h/2.1f,8,.5F,.5F,.5F);
			SE();
			// name
			batch.begin();
			layout.setText(font, this.tag);
			float x, y;
			if (this.tag.length() < 3) {    // harmonic labels to left
				x = this.x - layout.width - this.h;
				y = this.y + layout.height / 2;
			} else {                    // word labels above
				x = this.x + this.w / 2 - layout.width / 2;
				y = this.y + layout.height + 25;

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
			if (Gdx.input.justTouched()&&!click){
				if (mx > this.x - this.h && mx < this.x + this.w + this.h && my > this.y - this.h && my < this.y + this.h) {
					this.move = true;
					click = true;
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
				BS.set(this.tag,this.min,this.max,this.val,this.snap,this.show,this.display,this.SD);
			}
		}
	}

	private void credit(){
		batch.begin();
		String tagLine = "2017 Matt Craig, matt.simon.craig@gmail.com";
		layout.setText(creditFont,tagLine);
		//float x = Gdx.graphics.getWidth()-layout.width-10;
		float x = MIDX-layout.width/2;
		float y = layout.height+15;
		creditFont.draw(batch,tagLine,x,y);
		batch.end();
	}

	private BitmapFont font, creditFont, fontBig;
	Texture bb1,bb2,bb3,bb4,bb5,img;
	Sprite on,off,orange,minS,maxS,trash;


	private void init(){
	}

	private boolean vars = false;
	float mx, my;


	private void clearScreen() {
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}


	@Override
	public void create () {
		SCREENX = Gdx.graphics.getWidth();
		SCREENY = Gdx.graphics.getHeight();
		MIDX = SCREENX / 2;
		MIDY = SCREENY / 2;
		RBUT = SCREENX/60;
		BUTX = RBUT+20;
		BUTY = SCREENY-60;
		PI = (float)Math.PI;
		TD = 80; // touch distance
		FPS = 60;


		// set all buttons, sliders, objects here...




		camera = new OrthographicCamera(SCREENX,SCREENY);
		camera.position.set(SCREENX/2, SCREENY/2,0);
		camera.update();
		viewport = new FitViewport(SCREENX,SCREENY,camera);
		font = new BitmapFont(Gdx.files.internal("font2.fnt"));
		fontBig = new BitmapFont(Gdx.files.internal("font.fnt"));
		creditFont = new BitmapFont(Gdx.files.internal("creditFont.fnt"));
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		creditFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

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


	@Override
	public void render () {

		mx = (Gdx.input.getX());
		my = (viewport.getWorldHeight() - Gdx.input.getY());

		if (!vars){
			init();
			vars = true;
		}

		clearScreen();

		// main code here




		//
		if (BS.b){
			BS.go();
		}
		if (!Gdx.input.isTouched()){
			click = false;
		}
	}




	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
