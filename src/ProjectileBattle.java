// ProjectileBattle - battle - ProjectileBattle.java
// First created Sep 9, 2011 by Ethan Jennings

package battle;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.swing.*;

class Settings {
	static boolean debugVersion = false;
	static boolean smoothGraphics = true;
}

class Explosion
{ 
	private double x_;
	private double y_;
	private double size_;
	private double explosionWidth_;
	Explosion(double x, double y, double explosionWidth) {
		size_ = 0;
		x_ = x;
		y_ = y;
		explosionWidth_ = explosionWidth;
	}
	public boolean update() { // Returns true when the explosion is done
		size_ += 0.02;
		if (size_ >= 1)
			return true;
		else
			return false;
	}
	public double getX() {
		return x_;
	}
	public void setX(int x) {
		x_ = x;
	}
	public double getY() {
		return y_;
	}
	public void setY(int y) {
		y_ = y;
	}
	public double getSize() {
		return size_;
	}
	public void setSize(double size) {
		size_ = size;
	}
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		
		int red = (int)(255.0*((1-size_+0.5)));
		if (red > 255)
			red = 255;
		int green = (int)( 180.0*((1-size_+0.5)));
		if (green > 150)
			green = 150;
		g.setColor(new Color(red,green, (int)(100.0*(size_))));
		//g.fillOval((int)x_, (int)y_, (int)(size_*explosionWidth_*1.5), (int)(size_*explosionWidth_*1.5));
		g.fillOval((int)(x_-size_*(explosionWidth_/2)*1.5), (int)(y_-size_*(explosionWidth_/2)*1.5), (int)(size_*explosionWidth_*1.5), (int)(size_*explosionWidth_*1.5));
	}
}

class RevealedPortion
{
	private double x_;
	private double y_;
	private double radius_;
	public RevealedPortion(double x, double y, int radius) {
		x_ = x;
		y_ = y;
		radius_ = radius;
	}
	public double getX() {
		return x_;
	}
	public void setX(int x) {
		x_ = x;
	}
	public double getY() {
		return y_;
	}
	public void setY(int y) {
		y_ = y;
	}
	public double getRadius() {
		return (int) radius_;
	}
	public void setRadius(int radius) {
		radius_ = radius;
	}
}

class Projectile
{
	private double x_;
	private double y_;
	private double z_;
	private double vecX_;
	private double vecY_;
	private double vecZ_;
	private double radius_;
	public static final double GRAVITY = 9.81;
	Projectile(double x, double y, double bearing, double speed, double angle, double radius) {
		x_ = x;
		y_ = y;
		z_ = 0;
		radius_ = radius;
		launch(bearing,speed,angle);
	}
	public Projectile(Point2D.Double launchLocation, Point2D.Double target, double angle, double radius) {
		x_ = launchLocation.getX();
		y_ = launchLocation.getY();
		z_ = 0;
		radius_ = radius;
		launch(target,angle);
	}
	public Projectile(Point2D.Double launchLocation, double bearing, double range, double radius) {
		x_ = launchLocation.getX();
		y_ = launchLocation.getY();
		z_ = 0;
		radius_ = radius;
		launch(bearing,range);
	}
	public void launchWithoutBearing(double launchDirection, double speed, double angle) {
		launchDirection = (launchDirection*Math.PI)/180; // Convert to radians
		angle = (angle*Math.PI)/180;
		vecZ_ = speed * Math.sin(angle);
		double xySpeed = speed * Math.cos(angle);
		vecX_ = xySpeed * Math.cos(launchDirection);
		vecY_ = -xySpeed * Math.sin(launchDirection);
	}
	public void launch(double bearing, double speed, double angle) {
		bearing = -(bearing-90); // Convert away from bearing
		launchWithoutBearing(bearing,speed,angle);
	}
	public void launch(double bearing, double range) {
		double launchAngle = 45;
		double launchSpeed = Math.sqrt((range*GRAVITY)/Math.sin(Math.toRadians(2*launchAngle)));
		launch(bearing,launchSpeed,launchAngle);
	}
	public void launch(Point2D.Double target, double angle) {
		double launchDirection = getAngle(new Point2D.Double(target.getX() - x_, target.getY() - y_));
		double range = target.distance(new Point2D.Double(x_,y_));
		double launchAngle = angle;
		double launchSpeed = Math.sqrt((range*GRAVITY)/Math.sin(Math.toRadians(2*launchAngle)));
		launchWithoutBearing(launchDirection,launchSpeed,launchAngle);
	}
	private double getAngle(Point2D.Double vec) {
		double angle = 0;
		vec.y = -vec.y;
		if (vec.y == 0) {
			if (vec.x >= 0)
				angle = 0;
			else
				angle = 180;
		}
		else if (vec.x == 0) {
			if (vec.y >= 0)
				angle = 90;
			else
				angle = 270;
		}
		
		else if (vec.x > 0 && vec.y > 0)
			angle = Math.toDegrees(Math.atan(vec.y/vec.x));
		else if (vec.x < 0 && vec.y > 0)
			angle = 180 + Math.toDegrees(Math.atan(vec.y/vec.x));
		else if (vec.x < 0 && vec.y < 0)
			angle = 180 + Math.toDegrees(Math.atan(vec.y/vec.x));
		else if (vec.x > 0 && vec.y < 0)
			angle = 360 + Math.toDegrees(Math.atan(vec.y/vec.x));
		
		return angle;
	}
	public boolean update() { // Returns true if the projectile has hit
		x_ += vecX_/200;
		y_ += vecY_/200;
		z_ += vecZ_/200;
		vecZ_ -= GRAVITY/200;
		if (z_ < 0)
			return true;
		else
			return false;
	}
	public double getX() {
		return (x_-(getRadius())/2.0);
	}
	public void setX(double x) {
		x_ = x;
	}
	public double getY() {
		return (y_-(getRadius())/2.0);
	}
	public double getActualX() {
		return x_;
	}
	public double getActualY() {
		return y_;
	}
	public void setY(double y) {
		y_ = y;
	}
	public double getZ() {
		return z_;
	}
	public void setZ(double z) {
		z_ = z;
	}
	public double getRadius() {
		return (radius_*(z_/30+0.05));
	}
	public void setRadius(double radius) {
		radius_ = radius;
	}
}

class HitPortion {
	private Point pos_;
	private boolean hit_;
	public HitPortion(Point pos, boolean hit) {
		pos_ = pos;
		hit_ = hit;
	}
	public HitPortion(Point pos) {
		pos_ = pos;
		hit_ = false;
	}
	public Point getPos() {
		return pos_;
	}
	public void setPos(Point pos) {
		pos_ = pos;
	}
	public boolean isHit() {
		return hit_;
	}
	public void setHit(boolean hit) {
		hit_ = hit;
	}
}

class PossibleMove {
	private int x_;
	private int y_;
	enum Mode {UNREVEALED, EMPTY,HIT,SUNK};
	private Mode mode_;
	private double score_;
	public PossibleMove(int x, int y, Mode mode) {
		x_ = x;
		y_ = y;
		score_ = 0;
		mode_ = mode;
	}
	public int getX() {
		return x_;
	}
	public void setX(int x) {
		x_ = x;
	}
	public int getY() {
		return y_;
	}
	public void setY(int y) {
		y_ = y;
	}
	public double getScore() {
		return score_;
	}
	public void setScore(double score) {
		score_ = score;
	}
	public void addToScore(double score) {
		score_ += score;
	}
	public Mode getMode() {
		return mode_;
	}
	public void setMode(Mode mode) {
		mode_ = mode;
	}
	public void incrementScore() {
		score_++;
	}
}

class AIOpponent {
	private Player player_;
	private Player otherPlayer_;
	private Point2D.Double target_;
	private Random r_;
	private Rectangle grid_;
	private int cellSize_;
	private int gridWidth_;
	private int gridHeight_;
	private int moveDelay_;
	private boolean crossHairLocked_;
	enum Mode {WAITING,MOVING_CROSSHAIR};
	Mode mode_;
	AIOpponent(Player player, Player otherPlayer, Rectangle grid, int cellSize, int gridWidth, int gridHeight, Random r) {
		player_ = player;
		otherPlayer_ = otherPlayer;
		grid_ = grid;
		r_ = r;
		moveDelay_ = 1000;
		mode_ = Mode.WAITING;
		crossHairLocked_ = false;
		cellSize_ = cellSize;
		gridWidth_ = gridWidth;
		gridHeight_ = gridHeight;
	}
	public Mode getMode() {
		return mode_;
	}
	public void setMode(Mode mode) {
		mode_ = mode;
		if (mode == Mode.MOVING_CROSSHAIR)
			crossHairLocked_ = true;
	}
	public int getDelay() {
		return moveDelay_;
	}
	public void chooseTarget(boolean revealedRectangles[][], ArrayList<Score> scoreNumbers_) {
		scoreNumbers_.clear();
		PossibleMove possibleMoves[][] = otherPlayer_.getHitLocations();
		for (int x = 0; x < revealedRectangles.length/2; x++) {
			for (int y = 0; y <revealedRectangles[0].length; y++) {
				if (revealedRectangles[x][y] && possibleMoves[x][y].getMode() == PossibleMove.Mode.UNREVEALED)
					possibleMoves[x][y].setMode(PossibleMove.Mode.EMPTY);
			}
		}
		for (int x = 0; x < possibleMoves.length; x++) {
			for (int y = 0; y < possibleMoves[0].length; y++) {
				if (possibleMoves[x][y].getMode() == PossibleMove.Mode.HIT)
				{
					iterateOverBoard(possibleMoves,x,y,0); // Left
					iterateOverBoard(possibleMoves,x,y,1); // Top
					iterateOverBoard(possibleMoves,x,y,2); // Right
					iterateOverBoard(possibleMoves,x,y,3); // Bottom
				}
				if (possibleMoves[x][y].getMode() == PossibleMove.Mode.EMPTY)
					possibleMoves[x][y].setScore(-.5);
				else if (possibleMoves[x][y].getMode() == PossibleMove.Mode.HIT)
					possibleMoves[x][y].setScore(-.5);
				else if (possibleMoves[x][y].getMode() == PossibleMove.Mode.SUNK)
					possibleMoves[x][y].setScore(-.1);
					
			}
		}
		ArrayList<Point2D.Double> highestScoreLocations = new ArrayList<Point2D.Double>();
		double highestScore = -100;
		for (int x = 0; x < gridWidth_; x++) {
			for (int y = 0; y < gridHeight_; y++) {
				scoreNumbers_.add(new Score(possibleMoves[x][y].getScore(),new Point2D.Double(possibleMoves[x][y].getX(),possibleMoves[x][y].getY()+cellSize_/2)));
			}
		}
		for (int x = 0; x < gridWidth_; x++) {
			for (int y = 0; y < gridHeight_; y++) {
				if (possibleMoves[x][y].getScore() == 1 && x > 0 && y > 0 && x < gridWidth_-1 && y < gridHeight_-1) {
					possibleMoves[x][y].setScore(0.4);
				}
			}
		}
		// Find the highest scoring position
		for (int x = 0; x < gridWidth_-1; x++) {
			for (int y = 0; y < gridHeight_-1; y++) {
				double score = 0;
				score += possibleMoves[x][y].getScore();
				score += possibleMoves[x+1][y].getScore();
				score += possibleMoves[x][y+1].getScore();
				score += possibleMoves[x+1][y+1].getScore();
				if (score > highestScore) {
					highestScore = score;
					highestScoreLocations.clear();
					highestScoreLocations.add(new Point2D.Double(x+1,y+1));
				}
				else if (score == highestScore) {
					highestScoreLocations.add(new Point2D.Double(x+1,y+1));
				}
			}
		}
		Point2D.Double launchTarget = highestScoreLocations.get(r_.nextInt(highestScoreLocations.size()));
		target_ = finalizePosition(launchTarget);
	}
	public void iterateOverBoard(PossibleMove possibleMoves[][], int x, int y, int dir) {
		int orignalX = x;
		int originalY = y;
		int numIterations = 0;
		boolean done = false;
		while (possibleMoves[x][y].getMode() == PossibleMove.Mode.HIT) {
			switch (dir) {
				case 0: if (x > 0) x--; 			else done = true; break; // Left
				case 1: if (y > 0) y--; 			else done = true; break; // Up
				case 2: if (x < gridWidth_-1)  x++;	else done = true; break; // Right
				case 3: if (y < gridHeight_-1) y++;	else done = true; break; // Down
			}
			numIterations++;
			if (done) {
				break;
			}
		}
		if (possibleMoves[x][y].getMode() == PossibleMove.Mode.UNREVEALED) {
			possibleMoves[x][y].addToScore(1.0);
		}
		
	}
	public void iterateOverBoardEmptySpaces(PossibleMove possibleMoves[][], int x, int y, int dir) {
		int orignalX = x;
		int originalY = y;
		while (possibleMoves[x][y].getMode() == PossibleMove.Mode.EMPTY && x > 0 && y > 0 && x < gridWidth_-1 && y < gridHeight_-1) {
			switch (dir) {
				case 0: x--; break; // Left
				case 1: y--; break; // Up
				case 2: x++; break; // Right
				case 3: y++; break; // Down
			}
			break;
		}
		if (possibleMoves[x][y].getMode() == PossibleMove.Mode.UNREVEALED) {
			possibleMoves[x][y].addToScore(-0.2);
		}
	}
	// Add a little random offset to make the ai look more realistic and convert grid space to screen space 
	public Point2D.Double finalizePosition(Point2D.Double point) {
		point.x = gridSpaceToScreenSpaceX(point.x);
		point.y = gridSpaceToScreenSpaceY(point.y);
		//point.x += r_.nextDouble()*10-5;
		point.x += r_.nextDouble()*4-2;
		point.y += r_.nextDouble()*4-2;
		return point;
	}
	public Point2D.Double randomPosition() {
		Point2D.Double point = new Point2D.Double();
		point.x = r_.nextInt(gridWidth_-2)+1;
		point.y = r_.nextInt(gridHeight_-2)+1;
		return point;
	}
	public Point2D.Double getTarget() {
		return target_;
	}
	public boolean isCrossHairLocked() {
		return crossHairLocked_;
	}
	public void setCrossHairLocked(boolean locked) {
		crossHairLocked_ = locked;
	}
	public int gridSpaceToScreenSpaceX(double x) {
		return (int) Math.round(x*cellSize_+grid_.getX());
	}
	public int gridSpaceToScreenSpaceY(double y) {
		return (int) Math.round(y*cellSize_+grid_.getY());
	}
}

enum HitResult {
	MISS, HIT, SUNK
}

class Ship {
	private int x_;
	private int y_;
	private int lastX_;
	private int lastY_;
	private int width_;
	private int height_;
	private int gridWidth_;
	private int gridXOffset_;
	private int gridYOffset_;
	private int draggingPointX_;
	private int draggingPointY_;
	private boolean valid_;
	private boolean sunk_;
	private boolean moved_;
	private boolean firingShip_;
	private HitPortion[][] hitRectangles_;
	public Ship(int x, int y, int width, int height, int gridWidth, int xOffset, int yOffset) {
		x_ = x*gridWidth + xOffset;
		y_ = y*gridWidth + yOffset;
		width_ = width*gridWidth; 
		height_ = height*gridWidth;
		gridWidth_ = gridWidth;
		gridXOffset_ = xOffset;
		gridYOffset_ = yOffset;
		valid_ = false;
		moved_ = false;
		sunk_ = false;
	}
	public void setPos(int x, int y, int gridXOffset, int gridYOffset) {
		x_ = x*gridWidth_ + gridXOffset;
		y_ = y*gridWidth_ + gridYOffset;
	}
	public double getX() {
		return x_;
	}
	public double getY() {
		return y_;
	}
	public Point2D.Double getActualFiringPoint(int gridXOffset, int gridYOffset) {
		double x = (getCenter().x-gridXOffset)/(double)gridWidth_;
		double y = (getCenter().y-gridYOffset)/(double)gridWidth_;
		return new Point2D.Double(x, y);
	}
	public double getWidth() {
		return width_;
	}
	public double getHeight() {
		return height_;
	}
	public static boolean circleRectIntersection(double circleX, double circleY, double radius, Rectangle rect) {
		double circleDistanceX = Math.abs(circleX - rect.x - rect.width/2);
		double circleDistanceY = Math.abs(circleY - rect.y - rect.height/2);
		
		if (circleDistanceX > rect.width/2 + radius)
			return false;
		if (circleDistanceY > rect.height/2 + radius)
			return false;
		
		if (circleDistanceX < rect.width/2)
			return true;
		if (circleDistanceY < rect.height/2)
			return true;
		
		double cornerDistance = getDistSq(circleDistanceX - rect.width/2,circleDistanceY - rect.height/2);
		if (cornerDistance < radius*radius)
			return true;
		return false;
		
	}
	/*public static boolean circleRectIntersection(double circleX, double circleY, double radius, Rectangle rect) {
		Point2D.Double circlePos = new Point2D.Double(circleX,circleY);
		double radiusSq = radius*radius;
		if (rect.contains(circlePos))
			return true;
		if (pointLineSegmentDistance(circlePos,new Point2D.Double(rect.getX(), rect.getY()),new Point2D.Double(rect.getX(), rect.getMaxY())) <= radiusSq) // Left line
			return true;
		if (pointLineSegmentDistance(circlePos,new Point2D.Double(rect.getX(), rect.getY()),new Point2D.Double(rect.getMaxX(), rect.getY())) <= radiusSq) // Top line
			return true;
		if (pointLineSegmentDistance(circlePos,new Point2D.Double(rect.getMaxX(), rect.getY()),new Point2D.Double(rect.getMaxX(), rect.getMaxY())) <= radiusSq) // Right line
			return true;
		if (pointLineSegmentDistance(circlePos,new Point2D.Double(rect.getX(), rect.getMaxY()),new Point2D.Double(rect.getMaxX(), rect.getMaxY())) <= radiusSq) // Bottom line
			return true;
		return false;
	}
	public static double pointLineSegmentDistance(Point2D.Double point, Point2D.Double startLine, Point2D.Double endLine) {
		double xDistLine = endLine.x-startLine.x;
		double yDistLine = endLine.y-startLine.y;
		double xDistPoint = point.x-startLine.x;
		double yDistPoint = point.y-startLine.y;
		double dot = dotProduct(new Point2D.Double(xDistPoint,yDistPoint),new Point2D.Double(xDistLine,yDistLine));
		double xClosestPoint = xDistLine*dot+startLine.x;
		double yClosestPoint = yDistLine*dot+startLine.y;
		return getDistSq(xClosestPoint-point.x,yClosestPoint-point.y);
	}*/
	public static double getDistSq(double vecX, double vecY) {
		return vecX*vecX+vecY*vecY;
	}
	public static double dotProduct(Point2D.Double vec1, Point2D.Double vec2) {
		return vec1.x*vec2.x + vec1.y*vec2.y;
	}
	public HitResult checkCollision(double xPos, double yPos, double radius) {
		boolean hit = false;
		for (int x = 0; x < width_/gridWidth_; x += 1) {
			for (int y = 0; y < height_/gridWidth_; y += 1) {
				if (circleRectIntersection(xPos, yPos, radius, new Rectangle(hitRectangles_[x][y].getPos().x,hitRectangles_[x][y].getPos().y,gridWidth_,gridWidth_)))
				{
					if (!hitRectangles_[x][y].isHit()) {
						hitRectangles_[x][y].setHit(true);
						hit = true;
					}
				}
			}
		}
		checkHit();
		if (hit && sunk_)
			return HitResult.SUNK;
		if (hit)
			return HitResult.HIT;
		// else
			return HitResult.MISS;
	}
	public boolean checkHit() {
		boolean hit = true;
		for (int x = 0; x < width_/gridWidth_; x += 1) {
			for (int y = 0; y < height_/gridWidth_; y += 1) {
				if (!hitRectangles_[x][y].isHit()) {
					hit = false;
					break;
				}
			}
			if (!hit)
				break;
		}
		if (hit)
			sunk_ = true;
		return hit;
	}
	public boolean checkRectCollision(Rectangle rect) {
		return rect.intersects(getRect());
	}
	public boolean checkContainted(Rectangle rect) {
		return rect.contains(getRect());
	}
	public boolean checkPointCollision(double x, double y) {
		if (x < x_)
			return false;
		if (x > x_ + width_)
			return false;
		if (y < y_)
			return false;
		if (y > y_ + height_)
			return false;
		
		return true;
	}
	public void createHitRectangles() {
		int width = width_/gridWidth_;
		int height = height_/gridWidth_;
		hitRectangles_ = new HitPortion[width][height];
		for (int i = 0; i < width; i++) {
			hitRectangles_[i] =  new HitPortion[height];
			for (int j = 0; j < height; j++) {
				hitRectangles_[i][j] = new HitPortion(new Point(i*gridWidth_ + x_,j*gridWidth_ + y_));
			}
		}
	}
	public void draw(Graphics g, boolean showFiringShip) {
		Graphics2D g2d = (Graphics2D) g;
		if (!valid_ && moved_) {
			g.setColor(new Color(0x55aa0000));
		    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		}
		else
			g.setColor(new Color(0xff444433));
		if (firingShip_ && showFiringShip)
			g.setColor(new Color(0xff009900));
		g.fillRect(x_, y_, width_, height_);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
	public void drawX(Graphics g) {
		if (firingShip_) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(2.0f));
			g.setColor(Color.RED);
			Point p = getCenter();
			g.drawLine(p.x-10, p.y-10, p.x+10, p.y+10);
			g.drawLine(p.x-10, p.y+10, p.x+10, p.y-10);
			g2d.setStroke(new BasicStroke(1.1f));
		}
	}
	private Point getCenter() {
		return new Point((int)getRect().getCenterX(),(int)getRect().getCenterY());
	}
	public void setDraggingPoint(int x, int y) {
		moved_ = true;
		draggingPointX_ = snapToGrid(x,gridXOffset_)-x_;
		draggingPointY_ = snapToGrid(y,gridYOffset_)-y_;
	}
	public void drag(int x, int y) {
		moved_ = true;
		x_ = snapToGrid(x,gridXOffset_)-draggingPointX_;
		y_ = snapToGrid(y,gridYOffset_)-draggingPointY_;
	}
	public void rotate(int x, int y) {
		moved_ = true;
		x = snapToGrid(x,gridXOffset_);
		y = snapToGrid(y,gridYOffset_);
		int bottom = y_ + height_;
		int right = x_ + width_;
		
		int temp = width_;
		width_ = height_;
		height_ = temp;
		int oldX = x_;
		y_ = y - (x-x_);
		x_ = x -(bottom-y-1*gridWidth_);
		draggingPointX_ = x-x_;
		draggingPointY_ = y-y_;
	}
	public int snapToGrid(int x, int offset) {
		if (x-offset <= 0)
			return ((x-offset)/gridWidth_ - 1) * gridWidth_ + offset;
		else
			return (x-offset)/gridWidth_ * gridWidth_ + offset;
	}
	public Rectangle getRect() {
		return new Rectangle(x_,y_,width_,height_);
	}
	public boolean isValid() {
		return valid_;
	}
	public void setValid(boolean valid) {
		valid_ = valid;
	}
	public boolean isFiringShip() {
		return firingShip_;
	}
	public void setFiringShip(boolean firingShip) {
		firingShip_ = firingShip;
	}
	public void drawHit(Graphics g) {
		if (sunk_) {
			g.setColor(new Color(0xffff3300));
			g.fillRect(x_, y_, width_, height_);
		}
		else if (hitRectangles_ != null) {
			g.setColor(new Color(0xffcccc00));
			for (int x = 0; x < width_/gridWidth_; x += 1) {
				for (int y = 0; y < height_/gridWidth_; y += 1) {
					if (hitRectangles_[x][y].isHit())
						g.fillRect(hitRectangles_[x][y].getPos().x, hitRectangles_[x][y].getPos().y, gridWidth_, gridWidth_);
				}
			}
		}
	}
	public boolean isSunk() {
		return sunk_;
	}
	public HitPortion[][] getHitLocations() {
		return  hitRectangles_;
	}
}

class Player {
	private ArrayList<Ship> ships_;
	private Ship movingShip_;
	private Ship firingShip_;
	private Rectangle grid_;
	private int gridWidth_;
	Player(Rectangle grid, int shipStartX, int shipStartY, int gridWidth) {
		grid_ = grid;
		gridWidth_ = gridWidth;
		createShips(shipStartX,shipStartY);
	}
	public void createShips(int shipStartX, int shipStartY) {
		ships_ = new ArrayList<Ship>();
		ships_.add(new Ship(0,0,2,2,gridWidth_,shipStartX,shipStartY));
		ships_.add(new Ship(3,0,8,2,gridWidth_,shipStartX,shipStartY));
		ships_.add(new Ship(0,3,11,1,gridWidth_,shipStartX,shipStartY));
		ships_.add(new Ship(0,5,3,1,gridWidth_,shipStartX,shipStartY));
		ships_.add(new Ship(4,5,7,1,gridWidth_,shipStartX,shipStartY));
		ships_.add(new Ship(0,7,6,3,gridWidth_,shipStartX,shipStartY));
	}
	public PossibleMove[][] getHitLocations() {
		PossibleMove[][] possibleMoves = new PossibleMove[grid_.width/gridWidth_][grid_.height/gridWidth_];
		for (int x = 0; x < grid_.width/gridWidth_; x++) {
			for (int y = 0; y < grid_.height/gridWidth_; y++) {
				possibleMoves[x][y] = new PossibleMove(grid_.x + x*gridWidth_, grid_.y + y*gridWidth_, PossibleMove.Mode.UNREVEALED);
			}
		}
		for (Ship s : ships_) {
			if (!s.isSunk()) {
				for (HitPortion[] hpArray: s.getHitLocations()) {
					for (HitPortion hp: hpArray) {
						PossibleMove.Mode mode;
						if (s.isSunk())
							mode = PossibleMove.Mode.SUNK;
						if (hp.isHit())
							mode = PossibleMove.Mode.HIT;
						else
							mode = PossibleMove.Mode.UNREVEALED;
						possibleMoves[(int) ((hp.getPos().x-grid_.x)/gridWidth_)][(int) ((hp.getPos().y-grid_.y)/gridWidth_)].setMode(mode);
					}
				}
			}
		}
		return possibleMoves;
	}
	public void drawShips(Graphics g, boolean showFiringShip) {
		for (Ship ship : ships_) {
			ship.draw(g,showFiringShip);
		}
	}
	public void drawShipX(Graphics g) {
		if (firingShip_ != null)
			firingShip_.drawX(g);
	}
	public void mousePressed(int x, int y, boolean rightMouse) {
		for (Ship ship : ships_) {
			if (ship.checkPointCollision(x, y)) {
				
				if (rightMouse) {
					if (movingShip_ == null)
						ship.rotate(x,y);
					else
						movingShip_.rotate(x, y);
					break;
				}
				else {
					movingShip_ = ship;
					movingShip_.setDraggingPoint(x,y);
				}
			}
		}
		checkShips();
	}
	public void mouseReleased() {
		movingShip_ = null;
	}
	public void mouseDragged(int x, int y) {
		if (movingShip_ != null) {
			movingShip_.drag(x,y);
			checkShips();
		}
	}
	private boolean checkShip(Ship ship) {
		boolean collision = false;
		for (Ship ship2 : ships_) {
			if (ship != ship2 && ship.checkRectCollision(ship2.getRect())) {
				ship.setValid(false);
				collision = true;
			}
		}
		return collision;
	}
	private void checkShips() {
		for (Ship ship : ships_) {
			if (ship.checkContainted(grid_))
				ship.setValid(true);
			else
				ship.setValid(false);
		}
		for (Ship ship1 : ships_) {
			checkShip(ship1);
		}
	}
	public boolean shipsInPlace() {
		for (Ship ship : ships_) {
			if (!ship.isValid())
				return false;
		}
		for (Ship ship : ships_) {
			ship.createHitRectangles();
		}
		return true;
	}
	public void pickFiringShip(Random rand) {
		ArrayList<Ship> shipList = new ArrayList<Ship>();
		for (Ship ship : ships_) {
			ship.setFiringShip(false);
			if (!ship.isSunk())
				shipList.add(ship);
		}
			int num = rand.nextInt(shipList.size());
			firingShip_ = shipList.get(num);
			firingShip_.setFiringShip(true);
	}
	public void randomArrangement(Random rand) {
		boolean startOver;
		do {
			startOver = false;
			for (Ship ship : ships_) {
				ship.setPos(1000,1000,grid_.x,grid_.y); // Arbitrary position away from grid
			}
			for (Ship ship : ships_) {
				boolean cont;
				int numTries = 0;
				do {
					cont = false;
					int x = rand.nextInt(grid_.width/gridWidth_);
					int y = rand.nextInt(grid_.height/gridWidth_);
					ship.setPos(x,y,grid_.x,grid_.y);
					int numRot = rand.nextInt(4);
					ship.setDraggingPoint(rand.nextInt(ship.getRect().width)+ship.getRect().x, rand.nextInt(ship.getRect().height)+ship.getRect().y);
					for (int i = 0; i < numRot; i++)
						ship.rotate(x*gridWidth_, y*gridWidth_);
					if (checkShip(ship) || !ship.checkContainted(grid_)) {
						cont = true;
						if (numTries > 20) // Let's just start over, 20 times couldn't find a spot for it.
						{
							startOver = true;
							cont = false;
						}
					}
					else
					{
						ship.setValid(true);
					}
					numTries++;
				} while(cont);
				if (startOver)
					break;
			}
		} while (startOver);
		checkShips();
	}
	public Point2D.Double getFiringLocation(int gridXOffset, int gridYOffset) {
		if (firingShip_ == null)
			return null;
		return firingShip_.getActualFiringPoint(gridXOffset, gridYOffset);
	}
	public HitResult checkHit(double x, double y, double radius) {
		HitResult hit = HitResult.MISS;
		for (Ship s : ships_) {
			HitResult temp = s.checkCollision(x, y, radius);
			if (temp.compareTo(hit) > 0)
				hit = temp;
		}
		return hit;
	}
	public void drawHit(Graphics g) {
		for (Ship s : ships_) {
			s.drawHit(g);
		}
	}
	public boolean hasLost() {
		boolean lost = true;
		for (Ship s : ships_) {
			if (!s.isSunk())
				lost = false;
		}
		return lost;
	}
	public void reset(int startShipX, int startShipY) {
		ships_ = null;
		createShips(startShipX,startShipY);
	}
	public int getNumShipsRemaining() {
		int num = 0;
		for (Ship s : ships_) {
			if (!s.isSunk())
				num++;
		}
		return num;
	}
}

class Score {
	private double score_;
	private Point2D.Double pos_;
	Score(double score, Point2D.Double pos) {
		score_ = score;
		pos_ = pos;
	}
	public double getScore() {
		return score_;
	}
	public Point2D.Double getPos() {
		return pos_;
	}
}

class GridMessage {
	private Color color_;
	private float messageAlpha_;
	private float increaseSpeed_;
	private float decreaseSpeed_;
	private int increaseDelay_;
	private int decreaseDelay_;
	private long nextTime_;
	private boolean messageIncreasing_;
	private String message_;
	private Rectangle2D bounds_;
	private Font font_;
	private int vertOffest_;
	public GridMessage(String message, int increaseDelay, float increaseSpeed, int decreaseDelay, float decreaseSpeed, int vertOffset, Font font, Color c, Graphics g) {
		font_ = font;
		setMessage(message,c,g);
		messageIncreasing_ = false;
		increaseDelay_ = increaseDelay;
		decreaseDelay_ = decreaseDelay;
		increaseSpeed_ = increaseSpeed;
		decreaseSpeed_ = decreaseSpeed;
		messageAlpha_ = 0;
		nextTime_ = -1;
		vertOffest_ = vertOffset;
	}
	private void findBounds(Graphics g) {
		Font tempFont = g.getFont();
		g.setFont(font_);
		bounds_ =  g.getFontMetrics().getStringBounds(message_, g);
		g.setFont(tempFont);
	}
	public void setMessage(String message, Color c, Graphics g) {
		color_ = c;
		message_ = message;
		findBounds(g);
	}
	public void beginIncreasing() {
		messageIncreasing_ = true;
		nextTime_ = Calendar.getInstance().getTimeInMillis() + increaseDelay_;
	}
	public void reset() {
		messageAlpha_ = 0;
		nextTime_ = -1;
		messageIncreasing_ = false;
	}
	public void update() {
		if (nextTime_ > 0 && Calendar.getInstance().getTimeInMillis() > nextTime_) {
			if (messageIncreasing_) {
				messageAlpha_ += increaseSpeed_;
				if (messageAlpha_ >= 1) {
					messageAlpha_ = 1;
					messageIncreasing_ = false;
					nextTime_ = Calendar.getInstance().getTimeInMillis() + decreaseDelay_;
				}
			}
			else {
				messageAlpha_ -= decreaseSpeed_;
				if (messageAlpha_ <= 0) {
					reset();
				}
			}
		}
	}
	public void draw(Graphics g, int appletWidth, int appletHeight) {
		if (messageAlpha_ <= 0 || messageAlpha_ > 1)
			return;
		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, messageAlpha_));
		g.setFont(font_);
		g.setColor(color_);
		g.drawString(message_,(int)(appletWidth-bounds_.getWidth())/2,(int)(appletHeight+bounds_.getHeight()/2)/2 + vertOffest_);
	}
}

class Grid {
	public enum Mode  {
    	SHIP_SELECT_PLAYER1, SHIP_SELECT_PLAYER2, PLAYER1_TURN, PLAYER2_TURN
	}
	private int width_; // Grid width (in grid units)
	private int height_; // Grid height (in grid units)
	private int left_;
	private int top_;
	private int gridWidth_;
	private int gridHeight_;
	private int bottomBarHeight_;
	private int cellRealSize_; // In meters
	private int cellSize_;
	private int gridCharHeight_;
	private int gridCharWidth;
	private Font gridFont_;
	private Font questionFont_;
	private int questionCharHeight_;
	private int questionCharWidth_;
	private boolean fontMeasured_;
	private Point mouse_;
	private Projectile projectile_;
	private Explosion exp_;
	private boolean crossHairVisible_;
	private ArrayList<RevealedPortion> revealedList_;
	private ArrayList<Point> hitIllustrationRectangles_;
	private boolean revealedRectangles_[][];
	private Player player1_;
	private Player player2_;
	private boolean turn_; // false: player 1, true: player 2
	private float warningMessageAlpha;
	private float backgroundFlash_;
	private boolean onePlayer_;
	private AIOpponent ai_;
	private long nextAiTime_;
	private Point2D.Double crossHairPos_;
	private ArrayList<Score> scoreNumbers_;
	private boolean movingCrossHairToPlayer_;
	private boolean showingScores_;
	
	private GridMessage hitMsg_;
	private GridMessage shipSunkPositive_;
	private GridMessage shipSunkNegative_;
	private GridMessage missMsg_;
	private GridMessage youWinMsg_;
	private GridMessage youLoseMsg_;
	private GridMessage player1WinMsg_;
	private GridMessage player2WinMsg_;
	private GridMessage turnOverMsg_;
	private ArrayList<GridMessage> gridMessages_;
	
	private int numTurns_;
	private int maxHitsAllowed_ = 3;
	
	enum LimitedMode {NONE, VELOCITY, ANGLE};
	LimitedMode limitedMode_;
	
	final static double explosionWidth = 24.0;
	
	private Mode mode_;
	
	private ProjectileBattle callBackInterface_;
	
	private Random rand_;
	private boolean nextTurn_;
	private boolean gameOver_;
	
	public Grid(int width, int height, int cellLength, int x, int y, int maxWidth, int maxHeight, int bottomBarHeight, ProjectileBattle callBackInterface) {
		width_ = width;
		height_ = height;
		bottomBarHeight_ = bottomBarHeight;
		cellRealSize_ = cellLength;
		rand_ = new Random();
		calculateGridSize(x, y, maxWidth, maxHeight);
		fontMeasured_ = false;
		try {
			mouse_ = new Point(MouseInfo.getPointerInfo().getLocation());
		}
		catch (SecurityException se) {
			mouse_ = new Point(0,0);
		}
		crossHairVisible_ = true;
		revealedList_ = new ArrayList<RevealedPortion>();
		mode_ = Mode.SHIP_SELECT_PLAYER1;
		
		player1_ = new Player(new Rectangle(left_,top_,gridWidth_/2,gridHeight_),left_ + gridWidth_/2 + cellSize_*2,top_, cellSize_);
		player2_ = new Player(new Rectangle(left_ + gridWidth_/2,top_,gridWidth_/2,gridHeight_),left_ ,top_,cellSize_);
		
		callBackInterface_ = callBackInterface;
		
		gridMessages_ = new ArrayList<GridMessage>();
		Font messageFont = new Font("Arial", 0, 120);
		Font smallerMessageFont = new Font("Arial", 0, 100);
		Color green = new Color(0xff00dd00);
		Color red = new Color(0xffdd0000);
		Graphics g = callBackInterface_.getGraphics();
		hitMsg_ = new GridMessage("HIT!",500,0.02f,500,0.004f,100,messageFont,green,g);
		gridMessages_.add(hitMsg_);
		shipSunkPositive_ = new GridMessage("SHIP SUNK!",500,0.02f,500,0.004f,100,smallerMessageFont,green,g);
		gridMessages_.add(shipSunkPositive_);
		shipSunkNegative_ = new GridMessage("SHIP SUNK",100,0.02f,500,0.004f,100,smallerMessageFont,red,g);
		gridMessages_.add(shipSunkNegative_);
		missMsg_ = new GridMessage("MISS",500,0.02f,500,0.004f,100,messageFont,red,g);
		gridMessages_.add(missMsg_);
		youWinMsg_ = new GridMessage("YOU WIN!",500,0.01f,3000,0.001f,0,messageFont,Color.WHITE,g);
		gridMessages_.add(youWinMsg_);
		youLoseMsg_ = new GridMessage("YOU LOSE!",500,0.01f,3000,0.001f,0,messageFont,Color.WHITE,g);
		gridMessages_.add(youLoseMsg_);
		player1WinMsg_ = new GridMessage("PLAYER 1 WINS!",500,0.01f,4000,0.001f,0,smallerMessageFont,Color.WHITE,g);
		gridMessages_.add(player1WinMsg_);
		player2WinMsg_ = new GridMessage("PLAYER 2 WINS!",500,0.01f,4000,0.001f,0,smallerMessageFont,Color.WHITE,g);
		gridMessages_.add(player2WinMsg_);
		turnOverMsg_ = new GridMessage("END OF TURN",500,0.02f,500,0.004f,100,smallerMessageFont,Color.WHITE,g);
		gridMessages_.add(turnOverMsg_);
		
		numTurns_ = 0;
		
		limitedMode_ = LimitedMode.NONE;
		
		turn_ = false;
		
		hitIllustrationRectangles_ = new ArrayList<Point>();
		
		revealedRectangles_ = new boolean[gridWidth_/cellSize_][];
		for (int i = 0; i < revealedRectangles_.length; i++) {
			revealedRectangles_[i] = new boolean[gridHeight_/cellSize_];
			for(int j = 0; j < revealedRectangles_[0].length; j++) {
				revealedRectangles_[i][j] = false;
			}
		}
		
		ai_ = new AIOpponent(player2_,player1_,new Rectangle(left_,top_,gridWidth_/2,gridHeight_),cellSize_,(gridWidth_/cellSize_)/2,gridHeight_/cellSize_,rand_);


		
		nextTurn_ = false;
		crossHairPos_ = new Point2D.Double();
		movingCrossHairToPlayer_ = true;
		
		scoreNumbers_ = new ArrayList<Score>();
		showingScores_ = false;
		
		gameOver_ = false;
		
	}
	public void setLimitedMode(LimitedMode mode) {
		limitedMode_ = mode;
	}
	public void resetPlayer() {
		player1_.reset(left_,top_);
		player1_.randomArrangement(rand_);
		player1_.shipsInPlace();
		hitIllustrationRectangles_ = new ArrayList<Point>();
		revealedList_ = new ArrayList<RevealedPortion>();
		
	}
	public void draw(Graphics g, int appletWidth, int appletHeight) {
		synchronized (player1_) {
		Graphics2D g2d = (Graphics2D)g;
		
		if  (Settings.smoothGraphics) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		
		measureFonts(g);
		
		g.setColor(new Color(0xff001177));
		g.fillRect(0, 0, appletWidth, appletHeight);

		g.setFont(gridFont_);
		
		
		if (!gameOver_) {
			drawObscuredPortion(g, gridWidth_, gridHeight_);
			if ((mode_ == Mode.PLAYER1_TURN || mode_ == Mode.PLAYER2_TURN) && (!onePlayer_ || movingCrossHairToPlayer_)) {
				
				g.setColor(new Color(0xff333333));
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
				
				for (Point p : hitIllustrationRectangles_) {
					g.fillRect(p.x, p.y, cellSize_, cellSize_);
				}
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			}
			drawRevealed(g);
		}
		
		if (gameOver_) {
			player1_.drawShips(g,!nextTurn_);
			player2_.drawShips(g,!nextTurn_);
		}
		else {
			switch (mode_) {
				case SHIP_SELECT_PLAYER1:
					player1_.drawShips(g,false);
					g.setColor(new Color(0xffaaaaaa));
					g.setFont(new Font("Arial", 0, 16));
					g.drawString("Drag the ships to move them into position.",left_+gridWidth_/2+30,top_+gridHeight_/2+60);
					g.drawString("Right click to rotate. Press \'done\' when finished.",left_+gridWidth_/2+30,top_+gridHeight_/2+75);
					
					if (warningMessageAlpha > 0) {
						g.setFont(new Font("Arial", 0, 20));
						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, warningMessageAlpha));
						g.setColor(new Color(0xffaa0000));
						g.drawString("Ships incorrectly placed!",left_+gridWidth_/2+30,top_+gridHeight_/2+120);
						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
					}
				break;
				case SHIP_SELECT_PLAYER2:
					player2_.drawShips(g,false);
					g.setColor(new Color(0xffaaaaaa));
					g.setFont(new Font("Arial", 0, 16));
					g.drawString("Drag the ships to move them into position.",30,top_+gridHeight_/2+60);
					g.drawString("Right click to rotate. Press \'done\' when finished.",30,top_+gridHeight_/2+75);
					if (warningMessageAlpha > 0) {
						g.setFont(new Font("Arial", 0, 20));
						 g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, warningMessageAlpha));
						g.setColor(new Color(0xffaa0000));
						g.drawString("Ships incorrectly placed!",30,top_+gridHeight_/2+120);
						 g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
					}
				break;
				case PLAYER1_TURN:
					player1_.drawShips(g,!nextTurn_);
					break;
				case PLAYER2_TURN:
					player2_.drawShips(g,!nextTurn_);
					break;
			}
		}
		
		g.setColor(new Color(0xffffffff));
		
		player1_.drawHit(g);
		player2_.drawHit(g);
		/*switch (mode_) {
			case PLAYER1_TURN: player2_.drawHit(g); break;
			case PLAYER2_TURN: player1_.drawHit(g); break;
		}*/
		
		drawExplosionHits(g);
		
		g.setColor(new Color(0xffaaaa00));
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		
		
		player1_.drawHit(g);
		player2_.drawHit(g);
		

		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		
		drawExplosions(g);
		
		drawGridHalf(g, appletWidth, appletHeight, gridWidth_, gridHeight_);
		
		switch (mode_) {
			case PLAYER1_TURN: if (!nextTurn_ && (!onePlayer_ || !turn_)) player1_.drawShipX(g); break; 
			case PLAYER2_TURN: if (!nextTurn_ && (!onePlayer_ || !turn_)) player2_.drawShipX(g); break;
		}
		
		drawBottomText(g,gridWidth_,gridHeight_,appletWidth,appletHeight);
		
		for (GridMessage msg : gridMessages_) {
			msg.draw(g, appletWidth, appletHeight);
		}
			
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
		g.setColor(Color.WHITE);
		g.setFont(gridFont_);
		
		/*
		if (showingScores_) {
			for (Score s : scoreNumbers_) {
				//TODO:
				g.drawString(Double.toString(s.getScore()),(int)s.getPos().getX() + 3,(int)s.getPos().getY() + 5);
			}
		}*/
		
		drawProjectile(g);
		
		drawCrossHairs(g, appletWidth, appletHeight);
		
		g.setColor(new Color(0xfff0f0f0));
		g.fillRect(0,appletHeight,appletWidth,bottomBarHeight_);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, backgroundFlash_*backgroundFlash_*backgroundFlash_*0.8f));
		g.setColor(Color.white);
		g.fillRect(0, 0, appletWidth, appletHeight+bottomBarHeight_);
			
			
		}
		
	}
	public void calculateGridSize(int x, int y, int maxWidth, int maxHeight) {
		y += 30;
		x += 30;
		maxWidth-= 30;
		maxHeight -= 70;
		if (maxWidth / width_ < maxHeight / height_)
			cellSize_ = maxWidth / width_;
		else
			cellSize_ = maxHeight / height_;
		gridWidth_ = cellSize_*width_;
		gridHeight_ = cellSize_*height_;
		left_ = x + (maxWidth-gridWidth_)/2;
		top_ = y + (maxHeight-gridHeight_)/2;
	}
	private void measureFonts(Graphics g) {
		if (!fontMeasured_) {
			gridFont_ = new Font("Arial", 0, 12);
			FontMetrics metrics = g.getFontMetrics(gridFont_);
			gridCharWidth = (metrics.getWidths())['0'];
			gridCharHeight_ = metrics.getAscent();
			questionFont_ = new Font("Arial",0,200);
			metrics = g.getFontMetrics(questionFont_);
			questionCharWidth_ = (metrics.getWidths())['?'];
			questionCharHeight_ = metrics.getAscent();
			fontMeasured_ = true;
		}
	}
	private void drawGridHalf(Graphics g, int appletWidth, int appletHeight, int gridWidth, int gridHeight) {		
		switch (mode_) {
			case SHIP_SELECT_PLAYER1:
				g.setColor(new Color(0xffaaaaaa));
				g.setFont(gridFont_);
				drawGrid(g,0,width_/2,0,height_,gridWidth/2,gridHeight);
				break;
			case SHIP_SELECT_PLAYER2:
				g.setColor(new Color(0xffaaaaaa));
				g.setFont(gridFont_);
				drawGrid(g,width_/2,width_,0,height_,gridWidth/2,gridHeight);
				break;
			case PLAYER1_TURN:
			case PLAYER2_TURN:
				Graphics2D g2d = (Graphics2D)g;
				//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, gridFlash_*0.5f+0.5f));
				//g.setColor(new Color(Color.HSBtoRGB(gridHue_, 1.0f, 0.8f)));
				g.setColor(new Color(0xffaaaaaa));
				g.setFont(gridFont_);
				drawGrid(g,0,width_,0,height_,gridWidth,gridHeight);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				break;
		}
	}
	private void drawObscuredPortion(Graphics g, int gridWidth, int gridHeight) {
		g.setColor(new Color(0xff666666));
		if (mode_ == Mode.PLAYER1_TURN)
			g.fillRect(left_ + gridWidth/2, top_, gridWidth/2, gridHeight);
		else if (mode_ == Mode.PLAYER2_TURN)
			g.fillRect(left_, top_, gridWidth/2, gridHeight);
		
		g.setColor(new Color(0xff808080));
		g.setFont(questionFont_);
		if (mode_ == Mode.PLAYER1_TURN)
			g.drawString("???", left_ + gridWidth/2 + (gridWidth/2-questionCharWidth_*3)/2, top_ + gridHeight/2 + (gridHeight/2-questionCharHeight_));
		else if (mode_ == Mode.PLAYER2_TURN)
			g.drawString("???", left_ + (gridWidth/2-questionCharWidth_*3)/2, top_ + gridHeight/2 + (gridHeight/2-questionCharHeight_));
		
	}
	private void drawGrid(Graphics g, int startLineHoriz, int endLineHoriz, int startLineVert, int endLineVert, int gridWidth, int gridHeight) {
		for (int i = startLineVert; i <= endLineVert; i++) {
			g.drawLine(left_ + startLineHoriz*cellSize_, top_ + cellSize_*i, left_ + gridWidth + startLineHoriz*cellSize_, top_ + cellSize_*i);
			g.drawString(String.format("% 3d",cellRealSize_*i), left_ + startLineHoriz*cellSize_ - 25, top_ + cellSize_*i + gridCharHeight_/2);
		}
		for (int i = startLineHoriz; i <= endLineHoriz; i++) {
			g.drawLine(left_ + cellSize_*i, top_, left_ + cellSize_*i, top_ + gridHeight);
			g.drawString(String.format("% 3d",cellRealSize_*i), left_ + cellSize_*i - gridCharWidth - 2, top_ - 10);
		}
		
	}
	private void drawExplosions(Graphics g) {
		if (exp_ != null) {
			exp_.draw(g);
		}
	}
	private void drawRevealed(Graphics g) {
		g.setColor(new Color(0xff001177));
		if (!turn_) {
			for (int x = revealedRectangles_.length/2; x < revealedRectangles_.length; x++) {
				for (int y = 0; y < revealedRectangles_[0].length; y++) {
					if (revealedRectangles_[x][y])
					g.fillRect(x*cellSize_+left_, y*cellSize_+top_, cellSize_, cellSize_);
				}
			}
		}
		else {
			for (int x = 0; x < revealedRectangles_.length/2; x++) {
				for (int y = 0; y < revealedRectangles_[0].length; y++) {
					if (revealedRectangles_[x][y])
					g.fillRect(x*cellSize_+left_, y*cellSize_+top_, cellSize_, cellSize_);
				}
			}
		}
	}
	private void drawExplosionHits(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(new Color(0xff000044));
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		for (int i = 0; i < revealedList_.size(); i++) {
			g.fillOval((int)(revealedList_.get(i).getX()-revealedList_.get(i).getRadius()/2),(int)(revealedList_.get(i).getY()-revealedList_.get(i).getRadius()/2),(int)(revealedList_.get(i).getRadius()),(int)(revealedList_.get(i).getRadius()));
		}
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
	private void drawBottomText(Graphics g, int gridWidth, int gridHeight, int appletHeight, int appletWidth) {
		
		
		g.setColor(new Color(0xffaaaaaa));
		
		double mouseActualPos = screenPosToGridPosX(mouse_.x);
		Point2D.Double launchLocation = (!turn_ ? player1_.getFiringLocation(left_, top_) : player2_.getFiringLocation(left_, top_) );
		if (launchLocation != null) {
			launchLocation.x *= 2;
			launchLocation.y *= 2;
			g.drawString("Firing X =" + Double.toString(Math.round((launchLocation.getX()*10))/10.0), left_ + gridWidth - 190, top_ + gridHeight + 15);
			g.drawString("Firing Y =" + Double.toString(Math.round((launchLocation.getY()*10))/10.0), left_ + gridWidth - 190, top_ + gridHeight + 30);
		}
		g.drawString("Mouse X =" + Double.toString(Math.round((mouseActualPos*10000))/10000.0), left_ + gridWidth - 100, top_ + gridHeight + 15);
		mouseActualPos = screenPosToGridPosY(mouse_.y);
		g.drawString("Mouse Y =" + Double.toString(Math.round((mouseActualPos*10000))/10000.0), left_ + gridWidth - 100, top_ + gridHeight + 30);
		
		g.drawString("By: Ethan Jennings", 10, appletHeight - 10);
		
		if (mode_ == Mode.SHIP_SELECT_PLAYER1 || mode_ == Mode.SHIP_SELECT_PLAYER2)
			return;
		
		String shipString = new String();
		if (mode_ == Mode.PLAYER1_TURN)
			shipString += "Ships Remaining (You): ";
		else if (mode_ == Mode.PLAYER2_TURN)
			shipString += "Ships Remaining (Player 1): ";
		shipString += Integer.toString(player1_.getNumShipsRemaining());
		g.drawString(shipString, left_ + 100, top_ + gridHeight + 15);
		
		shipString = "";
		if (onePlayer_)
			shipString += "Ships Remaining (CPU): ";
		else if (mode_ == Mode.PLAYER2_TURN)
			shipString += "Ships Remaining (You): ";
		else if (mode_ == Mode.PLAYER1_TURN)
			shipString += "Ships Remaining (Player 2): ";
		
		shipString += Integer.toString(player2_.getNumShipsRemaining());
		g.drawString(shipString, left_ + 100, top_ + gridHeight + 30);
		
		g.setFont(new Font("Arial",Font.PLAIN,18));
		g.setColor(new Color(0xffaa0000));
		if (limitedMode_ == LimitedMode.ANGLE && (!onePlayer_ || movingCrossHairToPlayer_)) {
			g.drawString("Launch angle is stuck.", left_ + 300, top_ + gridHeight + 30);
		}
		else if (limitedMode_ == LimitedMode.VELOCITY && (!onePlayer_ || movingCrossHairToPlayer_)) {
			g.drawString("Launch speed is stuck.", left_ + 300, top_ + gridHeight + 30);
		}
	}
	private void drawProjectile(Graphics g) {
		if (projectile_ != null) {
			g.setColor(Color.YELLOW);
			g.fillOval(gridSpaceToScreenSpaceX(projectile_.getX()), gridSpaceToScreenSpaceY(projectile_.getY()), gridDistToScreenDist(projectile_.getRadius()), gridDistToScreenDist(projectile_.getRadius()));
		}
	}
	private void drawCrossHairs(Graphics g, int appletWidth, int appletHeight) {
		if (( crossHairVisible_ || onePlayer_ ) && (mode_ == Mode.PLAYER1_TURN || mode_ == Mode.PLAYER2_TURN)) {
			int posX;
			int posY;
			if (ai_.isCrossHairLocked() || movingCrossHairToPlayer_) {
				posX = (int) crossHairPos_.x;
				posY = (int) crossHairPos_.y;
			}
			else {
				posX = (int) mouse_.getX();
				posY = (int) mouse_.getY();
			}
			g.setColor(new Color(0xffaa0000));
			g.drawOval((int)(posX-explosionWidth/2), (int)(posY-explosionWidth/2), (int)(explosionWidth), (int)(explosionWidth));
			g.setColor(Color.RED);
			g.drawLine(posX, 0, posX, appletHeight);
			g.drawLine(0, posY, appletWidth, posY);
		}
	}
	public void setMouse(Point p) {
		mouse_ = p;
		hitIllustrationRectangles_.clear();
		switch (mode_) {
			case PLAYER1_TURN:
				for (int x = left_+gridWidth_/2; x < gridWidth_+left_; x += cellSize_) {
					for (int y = top_; y < gridHeight_+top_; y += cellSize_) {
						if (Ship.circleRectIntersection(p.x, p.y, explosionWidth/2, new Rectangle(x,y,cellSize_,cellSize_)))
						hitIllustrationRectangles_.add(new Point(x,y));
					}
				}
				break;
			case PLAYER2_TURN:
				for (int x = left_; x < gridWidth_/2+left_; x += cellSize_) {
					for (int y = top_; y < gridHeight_+top_; y += cellSize_) {
						if (Ship.circleRectIntersection(p.x, p.y, explosionWidth/2, new Rectangle(x,y,cellSize_,cellSize_)))
						hitIllustrationRectangles_.add(new Point(x,y));
					}
				}
				break;
		}
	}
	public void mousePressed(int x, int y,boolean rightMouseButton) {
		switch (mode_) {
			case SHIP_SELECT_PLAYER1:
				player1_.mousePressed(x, y,rightMouseButton);
				break;
			case SHIP_SELECT_PLAYER2:
				player2_.mousePressed(x, y,rightMouseButton);
				break;
			
			case PLAYER1_TURN: 
			case PLAYER2_TURN:
				if (Settings.debugVersion && rightMouseButton) {
					launch(new Point(x,y));
				}
				break;
			
		}
	}
	public void mouseDragged(int x, int y) {
		switch (mode_) {
		case SHIP_SELECT_PLAYER1:
			player1_.mouseDragged(x, y);
			break;
		case SHIP_SELECT_PLAYER2:
			player2_.mouseDragged(x, y);
			break;
		}
	}
	public void mouseReleased() {
		switch (mode_) {
		case SHIP_SELECT_PLAYER1:
			player1_.mouseReleased();
			break;
		case SHIP_SELECT_PLAYER2:
			player2_.mouseReleased();
			break;
		}
	}
	public void launch(Point screenPos) {
		//double mouseActualPosX = screenPosToGridPosX(screenPos.x);
		//double mouseActualPosY = screenPosToGridPosX(screenPos.y);
		//double bearing = 0.0f;
		if (projectile_ == null && !nextTurn_ && !gameOver_) {
			Point2D.Double target = new Point2D.Double(screenPosToGridPosX(screenPos.x),screenPosToGridPosY(screenPos.y));
			Point2D.Double launchLocation = (!turn_ ? player1_.getFiringLocation(left_, top_) : player2_.getFiringLocation(left_, top_) );
			launchLocation.x *= 2; launchLocation.y *= 2;
			projectile_ = new Projectile(launchLocation,target,45,10);
			callBackInterface_.setLaunchFieldsEnabled(false);
		}
		//launch(bearing,launchSpeed,launchAngle);
		
	}
	public void launch(double bearing, double range) {
		if (projectile_ == null && !nextTurn_ && !gameOver_) {
			Point2D.Double launchLocation = (!turn_ ? player1_.getFiringLocation(left_, top_) : player2_.getFiringLocation(left_, top_) );
			launchLocation.x *= 2; launchLocation.y *= 2;
			projectile_ = new Projectile(launchLocation,bearing,range,10);
			callBackInterface_.setLaunchFieldsEnabled(false);
		}
	}
	public void launch(double bearing, double speed, double angle) {
		if (projectile_ == null && !nextTurn_ && !gameOver_) {
			Point2D p = null;
			if (!turn_)
				p = player1_.getFiringLocation(left_,top_);
			else
				p = player2_.getFiringLocation(left_,top_);
			projectile_ = new Projectile(p.getX()*2,p.getY()*2,bearing,speed,angle,10);
		}
	}
	public void update() {
		if (movingCrossHairToPlayer_) {
			crossHairPos_.x += (mouse_.x-crossHairPos_.x)*0.1f;
			crossHairPos_.y += (mouse_.y-crossHairPos_.y)*0.1f;
		}
		if (onePlayer_ && nextTurn_ && !gameOver_) {
			
			if (ai_.getMode() == AIOpponent.Mode.WAITING && Calendar.getInstance().getTimeInMillis() > nextAiTime_)
			{
				ai_.setMode(AIOpponent.Mode.MOVING_CROSSHAIR);
				movingCrossHairToPlayer_ = false;
			}
			else if (ai_.getMode() == AIOpponent.Mode.MOVING_CROSSHAIR) {
				crossHairPos_.x += (ai_.getTarget().x-crossHairPos_.x)*0.03f;
				crossHairPos_.y += (ai_.getTarget().y-crossHairPos_.y)*0.03f;
				if ((Ship.getDistSq(crossHairPos_.x-ai_.getTarget().x, crossHairPos_.y-ai_.getTarget().y)) < 1) {
					crossHairPos_.setLocation(ai_.getTarget());
					exp_ = new Explosion(ai_.getTarget().x,ai_.getTarget().y,explosionWidth);
					HitResult player1Hit = player1_.checkHit(exp_.getX(),exp_.getY(),explosionWidth/2);
					player2_.checkHit(exp_.getX(),exp_.getY(),explosionWidth/2);
					HitResult hit = player1Hit;
					
					for (int x = 0; x < gridWidth_/cellSize_; x++) {
						for (int y = 0; y < gridHeight_/cellSize_; y++) {
							if (Ship.circleRectIntersection(exp_.getX(), exp_.getY(), explosionWidth/2, new Rectangle(x*cellSize_+left_,y*cellSize_+top_,cellSize_,cellSize_)))
								revealedRectangles_[x][y] = true;
						}
					}
					// Change
					if (hit == HitResult.MISS || numTurns_ == maxHitsAllowed_-1) {
						numTurns_ = 0;
						nextTurn_ = false;
						setTurn(false);
						if (numTurns_ == maxHitsAllowed_-1)
							backgroundFlash_ = 1.0f;
						callBackInterface_.setLaunchFieldsEnabled(true);
						ai_.setCrossHairLocked(false);
						movingCrossHairToPlayer_ = true;
						callBackInterface_.randomCalamity();
					}
					else if (hit == HitResult.HIT) {
						ai_.chooseTarget(revealedRectangles_,scoreNumbers_);
						nextAiTime_ = Calendar.getInstance().getTimeInMillis() + ai_.getDelay();
						backgroundFlash_ = 1.0f;
						ai_.setMode(AIOpponent.Mode.WAITING);
						numTurns_++;
					}
					else if (hit == HitResult.SUNK) {
						checkWin();
						backgroundFlash_ = 1.0f;
						shipSunkNegative_.beginIncreasing();
						if (!gameOver_) {
							ai_.chooseTarget(revealedRectangles_,scoreNumbers_);
							nextAiTime_ = Calendar.getInstance().getTimeInMillis() + ai_.getDelay();
						}
						ai_.setMode(AIOpponent.Mode.WAITING);
						numTurns_++;
					}
				}
			}
		}
		if (projectile_!= null && projectile_.update() && !gameOver_)
		{
			HitResult hit;
			exp_ = new Explosion(gridSpaceToScreenSpaceX(projectile_.getActualX()),gridSpaceToScreenSpaceY(projectile_.getActualY()),explosionWidth);
			HitResult player1Hit = player1_.checkHit(exp_.getX(),exp_.getY(),explosionWidth/2);
			HitResult player2Hit = player2_.checkHit(exp_.getX(),exp_.getY(),explosionWidth/2);
			if (!turn_)
				hit = player2Hit;
			else
				hit = player1Hit;
			
			if (hit == HitResult.MISS ||  numTurns_ == maxHitsAllowed_-1) {
				if (hit == HitResult.MISS)
					missMsg_.beginIncreasing();
				else {
					backgroundFlash_ = 1.0f;
					turnOverMsg_.beginIncreasing();
				}
				numTurns_ = 0;
				if (!onePlayer_) {
					callBackInterface_.changePanelMode(ProjectileBattle.PanelMode.NEXT_TURN);
					nextTurn_ = true;
				}
				else {
					ai_.chooseTarget(revealedRectangles_,scoreNumbers_);
					nextAiTime_ = Calendar.getInstance().getTimeInMillis() + ai_.getDelay();
					crossHairPos_.setLocation(mouse_);
					nextTurn_ = true;
				}
				ai_.setMode(AIOpponent.Mode.WAITING);
			}
			else if (hit == HitResult.HIT) {
				numTurns_++;
				backgroundFlash_ = 1.0f;
				callBackInterface_.setLaunchFieldsEnabled(true);
				hitMsg_.beginIncreasing();
			}
			else if (hit == HitResult.SUNK) {
				numTurns_++;
				backgroundFlash_ = 1.0f;
				callBackInterface_.setLaunchFieldsEnabled(true);
				shipSunkPositive_.beginIncreasing();
			}
			
			for (int x = 0; x < gridWidth_/cellSize_; x++) {
				for (int y = 0; y < gridHeight_/cellSize_; y++) {
					if (Ship.circleRectIntersection(exp_.getX(), exp_.getY(), explosionWidth/2, new Rectangle(x*cellSize_+left_,y*cellSize_+top_,cellSize_,cellSize_)))
						revealedRectangles_[x][y] = true;
				}
			}
			
			projectile_ = null;
			checkWin();
		}
		if (exp_ != null && exp_.update()) {
			revealedList_.add(new RevealedPortion(exp_.getX(),exp_.getY(),(int)explosionWidth));
			exp_ = null;
		}
		for (GridMessage msg : gridMessages_) {
			msg.update();
		}
		if (warningMessageAlpha > 0)
			warningMessageAlpha += 0.03;
		if (warningMessageAlpha > 1)
			warningMessageAlpha = 1;
		backgroundFlash_ -= 0.0055;
		if (backgroundFlash_ <= 0)
			backgroundFlash_ = 0.0f;
	}
	private void resetAllGridMessages() {
		for (GridMessage msg : gridMessages_) {
			msg.reset();
		}
	}
	private void checkWin() {
		if (onePlayer_) {
			if (player1_.hasLost()) {
				resetAllGridMessages();
				youLoseMsg_.beginIncreasing();
				gameOver_ = true;
				callBackInterface_.changePanelMode(ProjectileBattle.PanelMode.GAME_OVER);
				movingCrossHairToPlayer_ = true;
			}
			else if (player2_.hasLost()) {
				resetAllGridMessages();
				youWinMsg_.beginIncreasing();
				gameOver_ = true;
				movingCrossHairToPlayer_ = true;
				callBackInterface_.changePanelMode(ProjectileBattle.PanelMode.GAME_OVER);
			}
		}
		else {
			if (player1_.hasLost()) {
				resetAllGridMessages();
				player2WinMsg_.beginIncreasing();
				gameOver_ = true;
				movingCrossHairToPlayer_ = true;
				callBackInterface_.changePanelMode(ProjectileBattle.PanelMode.GAME_OVER);
			}
			else if (player2_.hasLost()) {
				resetAllGridMessages();
				player1WinMsg_.beginIncreasing();
				gameOver_ = true;
				movingCrossHairToPlayer_ = true;
				callBackInterface_.changePanelMode(ProjectileBattle.PanelMode.GAME_OVER);
			}
		}
	}
	public double screenPosToGridPosX(double val) {
		return (((val-left_)/gridWidth_))*width_*cellRealSize_;
	}
	public double screenPosToGridPosY(double val) {
		return (((val-top_)/gridHeight_))*height_*cellRealSize_;
	}
	public int gridDistToScreenDist(double val) {
		return (int) Math.round(val*cellSize_/cellRealSize_);
	}
	public int gridSpaceToScreenSpaceX(double x) {
		return (int) Math.round(x*cellSize_/cellRealSize_+left_);
	}
	public int gridSpaceToScreenSpaceY(double y) {
		return (int) Math.round(y*cellSize_/cellRealSize_+top_);
	}
	public boolean isCrossHairVisible() {
		return crossHairVisible_;
	}
	public void setCrossHairVisible(boolean crossHairVisible) {
		crossHairVisible_ = crossHairVisible;
	}
	public Mode getMode() {
		return mode_;
	}
	private void setTurn(boolean turn) {
		turn_ = turn;
		if (!turn_) {
			player1_.pickFiringShip(rand_);
		}
		else {
			player2_.pickFiringShip(rand_);
		}
		
	}
	private void setMode(Mode mode) {
		nextTurn_ = false;
		mode_ = mode;
		warningMessageAlpha = 0.0f;
		resetAllGridMessages();
		if (!onePlayer_) {
			switch (mode) {
				case SHIP_SELECT_PLAYER2:
					callBackInterface_.setMessage("Player 2, position your ships.","(don't look player 1)",ProjectileBattle.PanelMode.GAME_SHIP_SELECT);
					break;
				case PLAYER1_TURN:
					callBackInterface_.setMessage("It's your turn, player 1.","(don't look player 2)",ProjectileBattle.PanelMode.GAME);
					setTurn(false);
					break;
				case PLAYER2_TURN:
					callBackInterface_.setMessage("It's your turn, player 2.","(don't look player 1)",ProjectileBattle.PanelMode.GAME);
					setTurn(true);
					break;
			}
		}
		else {
			if (mode == Mode.PLAYER1_TURN) {
				setTurn(false);
				callBackInterface_.randomCalamity();
				callBackInterface_.changePanelMode(ProjectileBattle.PanelMode.GAME);
			}
			else if (mode == Mode.PLAYER2_TURN)
				callBackInterface_.randomCalamity();
				//setTurn(true);
		}
	}
	public boolean doneButtonPressed() { // Returns true 
		if (mode_ == Mode.SHIP_SELECT_PLAYER1) {
			if (player1_.shipsInPlace()) {
				if (!onePlayer_)
					setMode(Mode.SHIP_SELECT_PLAYER2);
				else {
					setMode(Mode.PLAYER1_TURN);
					player2_.randomArrangement(rand_);
					player2_.shipsInPlace();
				}
				return true;
			}
			else {
				warningMessageAlpha = 0.05f;
				return false;
			}
		}
		else if (mode_ == Mode.SHIP_SELECT_PLAYER2) {
			if (player2_.shipsInPlace()) {
				setMode(Mode.PLAYER1_TURN);
				return true;
			}
			else {
				warningMessageAlpha = 0.05f;
				return false;
			}
		}
		
		return false; // Should never be reached
		
	}
	public void randomArrangementPressed() {
		switch (mode_) {
			case SHIP_SELECT_PLAYER1:
				player1_.randomArrangement(rand_);
				break;
			case SHIP_SELECT_PLAYER2:
				player2_.randomArrangement(rand_);
		}
		
	}
	public void nextTurn() {
		setMode((turn_ ? Mode.PLAYER1_TURN : Mode.PLAYER2_TURN));
	}
	public boolean isOnePlayer() {
		return onePlayer_;
	}
	public void setOnePlayer(boolean onePlayer) {
		onePlayer_ = onePlayer;
	}
	public void toggleShowingScores() {
		showingScores_ = !showingScores_;
	}
}
class GamePanel extends JPanel implements MouseMotionListener, MouseListener  {

	private int width_;
    private int height_;
    private Grid grid_;
    
   
    public GamePanel(int width, int height, int bottomBarHeight, ProjectileBattle callBackInterface) {
    	width_ = width;
    	height_ = height;
    	grid_ = new Grid(30,20,2, 0, 0, width_, height_, bottomBarHeight, callBackInterface);
    	addMouseMotionListener((MouseMotionListener) this);
    	addMouseListener((MouseListener)this);
    }
    public Dimension getPreferredSize() {
        return new Dimension(width_,height_);
    }
    public Grid getGrid() {return grid_;}
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        grid_.draw(g, width_, height_);
    } 
    public void doLogic() {
    	grid_.update();
    }
    public void startLaunch(double bearing, double speed, double angle) {
    	grid_.launch(bearing, speed, angle);
    }
	public void startLaunch(double bearing, double range) {
		grid_.launch(bearing, range);
		
	}
    public void mouseMoved(MouseEvent event) {
    	grid_.setMouse(new Point(event.getX(),event.getY()));
    }
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
		grid_.mouseDragged(e.getX(), e.getY());
	}
	public void mouseEntered(MouseEvent e) {
		grid_.setMouse(new Point(e.getX(),e.getY()));
		grid_.setCrossHairVisible(true);
	}
	public void mouseExited(MouseEvent e) {
		grid_.setCrossHairVisible(false);
	}
	
	public void mousePressed(MouseEvent e) {
		grid_.mousePressed(e.getX(), e.getY(),e.getButton() == MouseEvent.BUTTON3);
	}
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			grid_.mouseReleased();
	}
	public void mouseClicked(MouseEvent e) {} // Unimplemented
	public boolean doneButtonPressed() {
		grid_.doneButtonPressed();
		return false;
	}
	public void randomArrangementPressed() {
		grid_.randomArrangementPressed();
	}
	public void nextTurn() {
		grid_.nextTurn();
	}
	public void setOnePlayer(boolean onePlayer) {
		grid_.setOnePlayer(onePlayer);
	}
	public void resetGrid(int bottomBarHeight, ProjectileBattle callBackInterface) {
		grid_ = null;
		grid_ = new Grid(30,20,2, 0, 0, width_, height_, bottomBarHeight, callBackInterface);
	}
}

class BackgroundPanel extends JPanel {
	public enum PanelMode {MENU, OPTIONS, HELP, MESSAGE_DISPLAY}
	PanelMode mode_;
	private int width_;
	private int height_;
	private ArrayList<Projectile> projectiles_;
	private ArrayList<Explosion> explosions_;
	private long nextProjectileTime_;
	private Font titleFont_;
	private Font textFont_;
	private Font smallFont_;
	private Font nameFont_;
	private Font infoFont_;
	private Random r_;
	private String title_;
	private String text_;
	private String smallerText_;
	Rectangle2D titleBounds_;
	Rectangle2D textBounds_;
	Rectangle2D smallerBounds_;
	GradientPaint gradient_;
	BackgroundPanel(int x, int y, int width, int height, Graphics g) {
		setBounds(x,y,width,height);
		width_ = width;
		height_ = height;
		projectiles_ = new ArrayList<Projectile>();
		explosions_ = new ArrayList<Explosion>();
		r_ = new Random();
		nextProjectileTime_ = 0;
		titleFont_ = new Font("Arial", Font.PLAIN, 70);
		textFont_ = new Font("Arial", Font.PLAIN, 30);
		nameFont_ = new Font("Arial", Font.PLAIN,12);
		infoFont_ = new Font("Arial",Font.PLAIN,16);
		smallFont_ = new Font("Arial",Font.PLAIN,22);
		gradient_ = new GradientPaint(0, 0, new Color(0xff001155), 0, height_, new Color(0xff002488),true);
		mode_ = PanelMode.MENU;
		title_ = "Projectile Battle";
		smallerText_ = null;
		Font tempFont = g.getFont();
		g.setFont(titleFont_);
		titleBounds_ = g.getFontMetrics().getStringBounds(title_, g);
		g.setFont(tempFont);
	}
	public void setText(String text, String smallerText, Graphics g) {
		text_ = text;
		Font tempFont = g.getFont();
		g.setFont(textFont_);
		textBounds_ = g.getFontMetrics().getStringBounds(text_, g);
		g.setFont(smallFont_);
		smallerText_ = smallerText;
		smallerBounds_ = g.getFontMetrics().getStringBounds(smallerText_, g);
		g.setFont(tempFont);
	}
	public void update() {
		synchronized (projectiles_) {
			if (mode_ == PanelMode.MENU) {
				if (Calendar.getInstance().getTimeInMillis() > nextProjectileTime_) {
					nextProjectileTime_ = Calendar.getInstance().getTimeInMillis() + r_.nextInt(3000)+50;
					Point2D.Double p = new Point2D.Double(-55,-10);
					double bearing = 0.0f;
					Point2D.Double target = new Point2D.Double(r_.nextDouble()*25,r_.nextDouble()*20);
					Point2D.Double launchLocation = null;
					boolean dir = r_.nextBoolean();
					if (!dir)
						launchLocation = new Point2D.Double(r_.nextDouble()*1-5,r_.nextDouble()*40-10);
					else
						launchLocation = new Point2D.Double(r_.nextDouble()*1+30,r_.nextDouble()*40-10);
					double launchAngle = r_.nextInt(20)+30;
					projectiles_.add(new Projectile(launchLocation,target,launchAngle,10));
				}
				for (int i = 0; i < projectiles_.size(); i++) {
					if(projectiles_.get(i).update())
					{
						explosions_.add(new Explosion(gridSpaceToScreenSpaceX(projectiles_.get(i).getActualX()), gridSpaceToScreenSpaceY(projectiles_.get(i).getActualY()),r_.nextDouble()*20+20));
						projectiles_.remove(i);
						i--;
					}
				}
				for (int i = 0; i < explosions_.size(); i++) {
					if (explosions_.get(i).update()) {
						explosions_.remove(i);
						i--;
					}
				}
			}
		}
		
	}
	 protected void paintComponent(Graphics g) {
		 Graphics2D g2d = (Graphics2D)g;
			
		 if (Settings.smoothGraphics) {
			 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
			 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		 }
		 g.setColor(new Color(0xff001177));
		 //long time = Calendar.getInstance().getTimeInMillis();
		 //g.setColor(new Color());
		 //g2d.setPaint(gradient_);
		 g.fillRect(0, 0, getWidth(), getHeight());
		 
		 //long elapsed = Calendar.getInstance().getTimeInMillis()-time;
		 
		 if (mode_ == PanelMode.MENU) {
			 g.setColor(new Color(0xff999999));
			 drawGrid(g,0,25,0,20,550,440,(height_-440)/2,(width_-550)/2,22);
			 
			 g.setFont(titleFont_);
			 g.setColor(new Color(0xffffffff));
			 g.drawString(title_,(int)(width_-titleBounds_.getWidth())/2,(int)(height_-titleBounds_.getHeight())/2-100);
			 
			
			 synchronized (projectiles_) {
			 for (Explosion exp: explosions_)
					exp.draw(g);
			 
				g.setColor(Color.YELLOW);		 
					 
				for (Projectile p : projectiles_)
				 	g.fillOval(gridSpaceToScreenSpaceX(p.getX()), gridSpaceToScreenSpaceY(p.getY()), gridDistToScreenDist(p.getRadius()), gridDistToScreenDist(p.getRadius()));
			
			 }
					
			g.setFont(nameFont_);
			g.setColor(Color.WHITE);
			g.drawString("Designed and programmed by Ethan Jennings, 2011. For Mr. Freeman.",10,height_-20);
			g.drawString("Java version: " + System.getProperty("java.version"),width_-200,height_-20);
		}
		else if (mode_ == PanelMode.MESSAGE_DISPLAY) {
			 g.setFont(textFont_);
			 g.setColor(new Color(0xffffffff));
			 g.drawString(text_,(int)(width_-textBounds_.getWidth())/2,(int)(height_-textBounds_.getHeight())/2-100);
			 g.setFont(smallFont_);
			 g.drawString(smallerText_, (int)(width_-smallerBounds_.getWidth())/2,(int)(height_-smallerBounds_.getHeight())/2-70);
		}
		else if (mode_ == PanelMode.OPTIONS) {
			 g.setFont(textFont_);
			 g.setColor(new Color(0xffffffff));	
			 g.drawString("Difficulty Options",(int)(width_-textBounds_.getWidth())/2,150);
			 
			 g.setFont(infoFont_);
			 g.drawString("Easier calculations involve bearing and range.", 150, 250);
			 g.drawString("Harder calcualtions involve bearing, launch velocity, and launch angle. Harder ", 150, 290);
			 g.drawString("calculations may also have the launch velocity or angle \'stuck\' at times.", 150, 308);
		}

		//g.setColor(Color.black);
		 //g.fillRect((int)linePos_, 0, getWidth(), getHeight());
	 }
	 public void setMode(PanelMode mode,Graphics g) {
		 mode_ = mode;
		 if (mode_ == PanelMode.OPTIONS)
			 setText("Difficulty Options","",g);
	 }
		public int gridSpaceToScreenSpaceX(double x) {
			int left = (width_-550)/2;
			return (int) Math.round(x*22/1+left);
		}
		public int gridSpaceToScreenSpaceY(double y) {
			int top = (height_-440)/2;
			return (int) Math.round(y*22/1+top);
		}
		public int gridDistToScreenDist(double val) {
			return (int) Math.round(val*22/1.0);
		}
	private void drawGrid(Graphics g, int startLineHoriz, int endLineHoriz, int startLineVert, int endLineVert, int gridWidth, int gridHeight, int top, int left, int cellSize) {
		for (int i = startLineVert; i <= endLineVert; i++) {
			g.drawLine(left + startLineHoriz*cellSize, top + cellSize*i, left + gridWidth + startLineHoriz*cellSize, top + cellSize*i);	
		}
		for (int i = startLineHoriz; i <= endLineHoriz; i++) {
			g.drawLine(left + cellSize*i, top, left + cellSize*i, top + gridHeight);
		}		
	}
}

public class ProjectileBattle extends JApplet implements Runnable, KeyListener, ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;

    private JButton launchButton_;
    private JButton doneButton_;
    private JButton randomArrangementButton_;
    private JButton okButton_;
    private JButton nextTurnButton_;
    private JButton onePlayerButton_;
    private JButton twoPlayerButton_;
    private JButton easyCalculationsButton_;
    private JButton harderCalculationsButton_;
    private JButton helpButton_;
    private JButton backButton_;
    private JButton yesButton_;
    private JButton noButton_;
    private JButton gameButton_;
    private JButton physicsButton_;
    private JButton smallBackButton_;
    private JScrollPane editorScrollPane_;
    private JTextPane helpText_;
    private GamePanel gamePanel_;
    private JFormattedTextField bearingField_;
    private JFormattedTextField speedField_;
    private JFormattedTextField rangeField_;
    private JFormattedTextField angleField_;
    private JLabel bearingLabel_; 
    private JLabel speedLabel_;
    private JLabel rangeLabel_;
    private JLabel angleLabel_;
    private BackgroundPanel backPanel_;
    private boolean easyCalculations_;
    
    private JFrame frame = new JFrame();
    private JLayeredPane lpane_ = new JLayeredPane();
    
    private int appletWidth_;
    private int appletHeight_;
    
    private Random rand_;
    
    public enum PanelMode {MENU, HELP, QUESTION, ONEPLAYER_OPTIONS, TWOPLAYER_OPTIONS, MESSAGE_DISPLAY, GAME, GAME_EASY, GAME_HARD, GAME_SHIP_SELECT, NEXT_TURN, GAME_OVER}
    PanelMode panelMode_;
    PanelMode nextPanelMode_;
    PanelMode lastMode_;
    
	public void init() {
		rand_ = new Random();
		
		panelMode_ = PanelMode.MENU;
		Dimension appletSize = this.getSize();
		appletHeight_ = appletSize.height;
		appletWidth_ = appletSize.width;
		
		easyCalculations_ = false;

		setFocusable(true);
		addKeyListener(this);
		
        gamePanel_ = new GamePanel(appletWidth_,appletHeight_-50,50,this);
        gamePanel_.setBounds(0,0,appletWidth_,appletHeight_);
       
        bearingLabel_ = new JLabel("Bearing:");
        bearingLabel_.setBounds(25,appletHeight_-35,75,20);
        speedLabel_ = new JLabel("Launch Speed:");
        speedLabel_.setBounds(195,appletHeight_-35,100,20);
        rangeLabel_ = new JLabel("Range:");
        rangeLabel_.setBounds(235,appletHeight_-35,100,20);
        angleLabel_ = new JLabel("Launch Angle:");
        angleLabel_.setBounds(405,appletHeight_-35,100,20);
        
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumIntegerDigits(8);
        nf.setMaximumFractionDigits(10);
        
        bearingField_ = new JFormattedTextField(nf);
        bearingField_.setValue(new Double(90));
        bearingField_.setColumns(8);
        bearingField_.setBounds(80,appletHeight_-35,100,20);
        
        speedField_ = new JFormattedTextField(nf);
        speedField_.setValue(new Double(15));
        speedField_.setColumns(8);
        speedField_.setBounds(290,appletHeight_-35,100,20);
        
        rangeField_ = new JFormattedTextField(nf);
        rangeField_.setValue(new Double(10));
        rangeField_.setColumns(8);
        rangeField_.setBounds(290,appletHeight_-35,100,20);
        
        angleField_ = new JFormattedTextField(nf);
        angleField_.setValue(new Double(45));
        angleField_.setColumns(8);
        angleField_.setBounds(500,appletHeight_-35,100,20);
        
        launchButton_ = new JButton("Launch!");
        launchButton_.addActionListener(this);
        launchButton_.setBounds(650,appletHeight_-40,100,30);
        
        nextTurnButton_ = new JButton("Next Turn");
        nextTurnButton_.setBounds((appletWidth_-100)/2,appletHeight_-40,100,30);
        nextTurnButton_.addActionListener(this);
        
        okButton_ = new JButton("Ok");
        okButton_.addActionListener(this);
		okButton_.setBounds((appletWidth_-150)/2,appletHeight_-320,150,50);
		
		yesButton_ = new JButton("Yes");
		yesButton_.addActionListener(this);
		yesButton_.setBounds((appletWidth_-150)/2-100,appletHeight_-320,150,50);
		
		noButton_ = new JButton("No");
		noButton_.addActionListener(this);
		noButton_.setBounds((appletWidth_-150)/2+100,appletHeight_-320,150,50);
        
        doneButton_ = new JButton("Done!");
        doneButton_.addActionListener(this);
		doneButton_.setBounds((appletWidth_-150)/2+90,appletHeight_-45,150,40);
        
        randomArrangementButton_ = new JButton("Randomize");
        randomArrangementButton_.addActionListener(this);
		randomArrangementButton_.setBounds((appletWidth_-150)/2-88,appletHeight_-45,150,40);
        
        onePlayerButton_ = new JButton("1 Player");
        onePlayerButton_.addActionListener(this);
        onePlayerButton_.setBounds((appletWidth_-150)/2, 220, 150, 50);
        
        twoPlayerButton_ = new JButton ("2 Player");
        twoPlayerButton_.addActionListener(this);
        twoPlayerButton_.setBounds((appletWidth_-150)/2,300,150,50);
        
        helpButton_ = new JButton ("Help");
        helpButton_.addActionListener(this);
        helpButton_.setBounds((appletWidth_-150)/2,380,150,50);
        
        easyCalculationsButton_ = new JButton("Easier Calculations");
        easyCalculationsButton_.addActionListener(this);
        easyCalculationsButton_.setBounds((appletWidth_-150)/2-100, 420, 150, 50);
        
        harderCalculationsButton_ = new JButton("Harder Calculations");
        harderCalculationsButton_.addActionListener(this);
        harderCalculationsButton_.setBounds((appletWidth_-150)/2+100, 420, 150, 50);
        
        backButton_ = new JButton("Back");
        backButton_.addActionListener(this);
		backButton_.setBounds((appletWidth_-150)/2,appletHeight_-50,150,30);
		
		smallBackButton_ = new JButton("Back");
		smallBackButton_.addActionListener(this);
		smallBackButton_.setBounds(20,appletHeight_-80,70,28);
		
		gameButton_ = new JButton("Gameplay");
		gameButton_.addActionListener(this);
		gameButton_.setBounds((appletWidth_-100)/2-80,10,100,28);
		
		physicsButton_ = new JButton("Physics");
		physicsButton_.addActionListener(this);
		physicsButton_.setBounds((appletWidth_-100)/2+80,10,100,28);
		
        helpText_ = new JTextPane();
		helpText_.setEditable(false);

		//helpText_.setBackground(new Color(0xff001177));
		
		editorScrollPane_ = new JScrollPane(helpText_);
		helpText_.setBackground(new Color(0xff001177));
		java.net.URL helpURL = getClass().getResource("/resources/HelpFile_Gameplay.html");
        try {
			helpText_.setPage(helpURL);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,"Error: " + e.getMessage() + " when loading help file.");
		}
        editorScrollPane_.setBackground(new Color(0xff001177));
        
        
        backPanel_ = new BackgroundPanel(0,0,appletWidth_,appletHeight_,getGraphics());

        frame.setPreferredSize(new Dimension(appletWidth_, appletHeight_));
        frame.setLayout(new BorderLayout());
        frame.add(lpane_, BorderLayout.CENTER);
        
        lpane_.setBounds(0, 0, appletWidth_, appletHeight_);
        lpane_.add(backPanel_,new Integer(0),0);
        frame.pack();
        setContentPane(lpane_);
        
        lpane_.add(gamePanel_, new Integer(1), 0);
		lpane_.add(onePlayerButton_, new Integer(2), 0);
		lpane_.add(twoPlayerButton_, new Integer(2), 0);
		lpane_.add(helpButton_, new Integer(2), 0);
		lpane_.add(backButton_, new Integer(2), 0);
		lpane_.add(smallBackButton_, new Integer(2), 0);
		lpane_.add(gameButton_, new Integer(2), 0);
		lpane_.add(physicsButton_, new Integer(2), 0);
		lpane_.add(okButton_, new Integer(2), 0);
		lpane_.add(yesButton_, new Integer(2), 0);
		lpane_.add(noButton_, new Integer(2), 0);
		lpane_.add(doneButton_,new Integer(2),0);
		lpane_.add(randomArrangementButton_,new Integer(2),0);
		lpane_.add(bearingLabel_,new Integer(2));
		lpane_.add(bearingField_,new Integer(2));
		lpane_.add(speedLabel_,new Integer(2));
		lpane_.add(speedField_,new Integer(2));
		lpane_.add(rangeLabel_,new Integer(2));
		lpane_.add(rangeField_,new Integer(2));
		lpane_.add(angleField_,new Integer(2));
		lpane_.add(angleLabel_,new Integer(2));
		lpane_.add(launchButton_,new Integer(2));
		lpane_.add(nextTurnButton_, new Integer(2));
		lpane_.add(easyCalculationsButton_, new Integer(2));
		lpane_.add(harderCalculationsButton_, new Integer(3));
		
		editorScrollPane_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane_.setBounds((appletWidth_-600)/2,50,600,450);
		editorScrollPane_.setVisible(false);
		lpane_.add(editorScrollPane_,new Integer(20),0);
		
		changePanelMode(PanelMode.MENU);
		
        Thread th = new Thread (this);
        th.setPriority(Thread.MAX_PRIORITY);
		th.start ();
        setVisible(true);
	}

	public void setMessage(String string, String smallerMessage, PanelMode nextPanelMode) {
		backPanel_.setText(string,smallerMessage,getGraphics());
		changePanelMode(PanelMode.MESSAGE_DISPLAY);
		nextPanelMode_ = nextPanelMode;
	}
	public void setLaunchFieldsEnabled(boolean enabled) {
		bearingLabel_.setEnabled(enabled);
		bearingField_.setEditable(enabled);
		speedLabel_.setEnabled(enabled);
		speedField_.setEditable(enabled);
		rangeLabel_.setEnabled(enabled);
		rangeField_.setEditable(enabled);
		angleLabel_.setEnabled(enabled);
		angleField_.setEditable(enabled);
		launchButton_.setEnabled(enabled);
	}
	public void changePanelMode(PanelMode panelMode) {
		panelMode_ = panelMode;
		if (panelMode_ == PanelMode.GAME)
			panelMode_ = (easyCalculations_ ? PanelMode.GAME_EASY : PanelMode.GAME_HARD);
		for (Component c : lpane_.getComponents()) {
			if (c != gamePanel_)
			c.setVisible(false);
		}
		backPanel_.setVisible(true);
		switch (panelMode_) {
			case MENU:
				onePlayerButton_.setVisible(true);
				twoPlayerButton_.setVisible(true);
				helpButton_.setVisible(true);
				backPanel_.setMode(BackgroundPanel.PanelMode.MENU,getGraphics());
				gamePanel_.setVisible(false);
				break;
			case HELP:
				gameButton_.setVisible(true);
				physicsButton_.setVisible(true);
				backButton_.setVisible(true);
				editorScrollPane_.setVisible(true);
				backPanel_.setMode(BackgroundPanel.PanelMode.HELP,getGraphics());
				gamePanel_.setVisible(false);
				break;
			case NEXT_TURN:
				gamePanel_.setVisible(true);
				nextTurnButton_.setVisible(true);
				break;
			case MESSAGE_DISPLAY:
				okButton_.setVisible(true);
				backPanel_.setMode(BackgroundPanel.PanelMode.MESSAGE_DISPLAY,getGraphics());
				gamePanel_.setVisible(false);
				break;
			case GAME_SHIP_SELECT:
				gamePanel_.setVisible(true);
				doneButton_.setVisible(true);
				randomArrangementButton_.setVisible(true);
				smallBackButton_.setVisible(true);
		        break;
			case ONEPLAYER_OPTIONS:
			case TWOPLAYER_OPTIONS:
				backPanel_.setMode(BackgroundPanel.PanelMode.OPTIONS,getGraphics());
				easyCalculationsButton_.setVisible(true);
				harderCalculationsButton_.setVisible(true);
				backButton_.setVisible(true);
				gamePanel_.setVisible(false);
				break;
			case QUESTION:
				backPanel_.setText("Are you sure you want to quit this game?","",getGraphics());
				backPanel_.setMode(BackgroundPanel.PanelMode.MESSAGE_DISPLAY,getGraphics());
				yesButton_.setVisible(true);
				noButton_.setVisible(true);
				gamePanel_.setVisible(false);
				break;
			case GAME_EASY:
				setLaunchFieldsEnabled(true);
				gamePanel_.setVisible(true);
				bearingLabel_.setVisible(true);
				bearingField_.setVisible(true);
				rangeLabel_.setVisible(true);
				rangeField_.setVisible(true);
				launchButton_.setVisible(true);
				smallBackButton_.setVisible(true);
		        break;
			case GAME_OVER:
				gamePanel_.setVisible(true);
				nextTurnButton_.setVisible(true);
				nextTurnButton_.setText("Back");
				break;
			case GAME_HARD:
				setLaunchFieldsEnabled(true);
				gamePanel_.setVisible(true);
				bearingLabel_.setVisible(true);
				bearingField_.setVisible(true);
				speedLabel_.setVisible(true);
				speedField_.setVisible(true);
				angleLabel_.setVisible(true);
				angleField_.setVisible(true);
				launchButton_.setVisible(true);
				smallBackButton_.setVisible(true);
				randomCalamity();
				break;
		}
		validate();
	}

	public void randomCalamity() {
		if (!easyCalculations_) {
			setLaunchFieldsEnabled(true);
			int randNum = rand_.nextInt(3);
			if (randNum == 1) {
				speedField_.setEditable(false);
				double randomSpeed = rand_.nextDouble()*5.0+25.0;
				randomSpeed = ((int)(randomSpeed*10))/10.0;
				speedField_.setText(Double.toString(randomSpeed));
				gamePanel_.getGrid().setLimitedMode(Grid.LimitedMode.VELOCITY);
			}
			else if (randNum == 2) {
				angleField_.setEditable(false);
				double randomAngle = rand_.nextDouble()*40.0+25.0;
				randomAngle = ((int)(randomAngle*10))/10.0;
				angleField_.setText(Double.toString(randomAngle));
				gamePanel_.getGrid().setLimitedMode(Grid.LimitedMode.ANGLE);
			}
			else {
				gamePanel_.getGrid().setLimitedMode(Grid.LimitedMode.NONE);
			}
		}
	}
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == launchButton_)
        {
        	if (easyCalculations_) {
        		double bearing = Double.parseDouble(removeCommas(bearingField_.getText()));
	        	double range = Double.parseDouble(removeCommas(rangeField_.getText()));
	        	if (range > 80){
	        		range = 80;
	        		rangeField_.setText(Double.toString(range));
	        	}
	        	gamePanel_.startLaunch(bearing, range);
        	}
        	else {
        		double bearing = Double.parseDouble(removeCommas(bearingField_.getText()));
	        	double speed = Double.parseDouble(removeCommas(speedField_.getText()));
	        	double angle = Double.parseDouble(removeCommas(angleField_.getText()));
	        	if (speed > 40){
	        		speed = 40;
	        		speedField_.setText(Double.toString(speed));
	        	}
	        	gamePanel_.startLaunch(bearing, speed, angle);
        	}
	        setBottomEnabled(true);
        }
        else if (e.getSource() == nextTurnButton_) {
        	if (panelMode_ == PanelMode.GAME_OVER) {
        		changePanelMode(PanelMode.MENU);
        		gamePanel_.resetGrid(50,this);
        	}
        	else
        		gamePanel_.nextTurn();
        }
        else if (e.getSource() == backButton_) {
        	changePanelMode(PanelMode.MENU);
        }
        else if(e.getSource() == gameButton_) {
    		helpText_.setBackground(new Color(0xff001177));
    		java.net.URL helpURL = getClass().getResource("/resources/HelpFile_Gameplay.html");
            try {
    			helpText_.setPage(helpURL);
    		} catch (IOException e1) {
    			e1.printStackTrace();
    			JOptionPane.showMessageDialog(frame,"Error: " + e1.getMessage() + " when loading help file.");
    		}
        }
        else if(e.getSource() == physicsButton_) {
    		helpText_.setBackground(new Color(0xff001177));
    		java.net.URL helpURL = getClass().getResource("/resources/HelpFile_Physics.html");
            try {
    			helpText_.setPage(helpURL);
    		} catch (IOException e1) {
    			e1.printStackTrace();
    			JOptionPane.showMessageDialog(frame,"Error: " + e1.getMessage() + " when loading help file.");
    		}
        }
        else if (e.getSource() == smallBackButton_) {
        	lastMode_ = panelMode_;
        	changePanelMode(PanelMode.QUESTION);
        }
        else if (e.getSource() == yesButton_) {
        	changePanelMode(PanelMode.MENU);
        	gamePanel_.resetGrid(50,this);
        }
        else if (e.getSource() == noButton_) {
        	changePanelMode(lastMode_);
        }
        else if (e.getSource() == okButton_) {
        	changePanelMode(nextPanelMode_);
        	randomCalamity();
        }
        else if (e.getSource() == onePlayerButton_) {
        	changePanelMode(PanelMode.ONEPLAYER_OPTIONS);
        }
        else if (e.getSource() == twoPlayerButton_) {
        	//
        	changePanelMode(PanelMode.TWOPLAYER_OPTIONS);
        }
        else if (e.getSource() == easyCalculationsButton_ || e.getSource() == harderCalculationsButton_) {
        	if (panelMode_ == PanelMode.ONEPLAYER_OPTIONS) {
        		gamePanel_.setOnePlayer(true);
        		changePanelMode(PanelMode.GAME_SHIP_SELECT);
        	}
        	else {
        		gamePanel_.setOnePlayer(false);
        		setMessage("Player 1, position your ships.","(don't look player 2)",PanelMode.GAME_SHIP_SELECT);
        	}
        	easyCalculations_ = (e.getSource() == easyCalculationsButton_);
        }
        else if (e.getSource() == helpButton_) {
        	changePanelMode(PanelMode.HELP);
        }
        else if (e.getSource() == doneButton_) {
        	if (gamePanel_.doneButtonPressed())
        	{
        		changePanelMode(PanelMode.GAME);
        	}
        }
        else if (e.getSource() == randomArrangementButton_) {
        	gamePanel_.randomArrangementPressed();
        }
        
    }

	private void setBottomEnabled(boolean val) {
		launchButton_.setEnabled(val);
		bearingField_.setEnabled(val);
		speedField_.setEnabled(val);
		angleField_.setEnabled(val);
	}
	
	public void start() { }

	
	public void stop() { }

	
	public void destroy() { }
	
	public void run ()
	{
		long lastTime;
	    while (true)
	    {
	    	lastTime = System.currentTimeMillis();
	        repaint();
	        if (gamePanel_.isVisible())
	        	gamePanel_.doLogic();
	        if (backPanel_.isVisible())
	        	backPanel_.update();
	        long waitTime = 0;
	        long difference = System.currentTimeMillis()-lastTime;
	        if (difference >= 5)
	        	waitTime = 5;
	        else 
	        	waitTime = 5 - difference;
	        try
	        { Thread.sleep(waitTime);} catch (InterruptedException ex) {}
	    } 

	}
	
	private static String removeCommas(String s) {
		for (int i = 0; i < s.length();i++) {
			if (s.charAt(i) == ',') {
				s = s.substring(0,i) + s.substring(i+1,s.length());
				i--;
			}
		}
		return s; 
	}


	@Override
	public void itemStateChanged(ItemEvent arg0) {
	}

	@Override
	public void keyPressed(KeyEvent e) { 
		if (Settings.debugVersion && e.getKeyCode() == KeyEvent.VK_CONTROL) {
		gamePanel_.getGrid().toggleShowingScores();
	}
	}

	@Override
	public void keyReleased(KeyEvent e) { // Unimplemented
	}

	@Override
	public void keyTyped(KeyEvent e) { // Unimplemented
	}

	
}
