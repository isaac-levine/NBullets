import tester.*;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.util.Random;

//the NBullets game, based off of 10 bullets
class NBullets extends World {
  ILoBullet bullets;
  ILoShip ships;
  int bulletsLeft;
  int shipsDestroyed;
  int ticksSinceLastSpawn;
  Random rand;

  NBullets(ILoBullet bullets, ILoShip ships, int bulletsLeft, int shipsDestroyed,
      int ticksSinceLastSpawn, Random rand) {
    this.bullets = bullets;
    this.ships = ships;
    this.bulletsLeft = bulletsLeft;
    this.shipsDestroyed = shipsDestroyed;
    this.ticksSinceLastSpawn = ticksSinceLastSpawn;
    this.rand = rand;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ...this.bullets... - ILoBullet
   * ...this.ships... - ILoShip
   * ...this.bulletsLeft... - int
   * ...this.shipsDestroyed... - int
   * ...this.ticksSinceLastSpawn... - int
   * ...this.rand... - Random
   *
   * Methods:
   * ...this.makeScene()... - WorldScene
   * ...this.displayInfo()... - WorldScene
   * ...this.onTick()... - NBullets
   * ...this.updatePos()... - NBullets
   * ...this.removeOffscreen()... - NBullets
   * ...this.explosions()... - NBullets
   * ...this.onKeyEvent(String)... - NBullets
   * ...this.spawnShips()... - NBullets
   * ...this.onKeyEvent(String)... - NBullets
   * ...this.worldEnds()... - WorldEnd
   * ...this.finalScene()... - WorldScene
   */

  NBullets(ILoBullet bullets, ILoShip ships, int bulletsLeft, int shipsDestroyed,
      int ticksSinceLastSpawn) {
    this(bullets, ships, bulletsLeft, shipsDestroyed, ticksSinceLastSpawn, new Random());
  }

  NBullets(int bullets) {
    this(new MtLoBullet(), new MtLoShip(), bullets, 0, 0);
  }

  public WorldScene makeScene() {
    return this.displayInfo(bullets.draw(ships.draw(this.getEmptyScene())));
  }

  WorldScene displayInfo(WorldScene scene) {
    // text to display
    String str = "bullets left: " + this.bulletsLeft + "; ships destroyed: " + this.shipsDestroyed;
    WorldImage text = new TextImage(str, 13, Color.BLACK);
    return scene.placeImageXY(text, 100, 290);
  }

  public NBullets onTick() {
    // update positions based on velocity
    // remove off screen bullets
    // remove off screen ships
    // check for ship collisions
    // check for bullet-ship collisions (explosions)
    // spawn ships
    return this.updatePos().removeOffscreen().explosions().spawnShips();
  }

  // update positions of all objects in the game
  NBullets updatePos() {
    return new NBullets(this.bullets.updatePos(), this.ships.updatePos(), this.bulletsLeft,
        this.shipsDestroyed, this.ticksSinceLastSpawn, this.rand);
  }

  // removes all off screen objects
  NBullets removeOffscreen() {
    return new NBullets(this.bullets.removeOffscreen(), this.ships.removeOffscreen(),
        this.bulletsLeft, this.shipsDestroyed, this.ticksSinceLastSpawn, this.rand);
  }

  // removes ships and bullets that are in contact with one another
  NBullets explosions() {
    return new NBullets(this.bullets.explodeBullets(this.ships),
        this.ships.explodeShips(this.bullets), this.bulletsLeft,
        this.shipsDestroyed + this.ships.shipsDestroyed(this.bullets),
        this.ticksSinceLastSpawn, this.rand);
  }

  // randomly spawns 1-3 new ships on the edges of the screen every 28 ticks
  NBullets spawnShips() {
    if (this.ticksSinceLastSpawn < 28) {
      return new NBullets(this.bullets, this.ships, this.bulletsLeft, this.shipsDestroyed,
          this.ticksSinceLastSpawn + 1);
    } else {
      // determine how many ships to spawn
      int numShips = this.rand.nextInt(3) + 1;
      return new NBullets(this.bullets, this.ships.spawnNewShips(numShips, rand), this.bulletsLeft,
          this.shipsDestroyed, 0, this.rand);
    }
  }

  // create new bullet at bottom middle of screen if space bar pressed and enough
  // bullets left
  public NBullets onKeyEvent(String key) {
    if (key.equals(" ") && this.bulletsLeft > 0) {
      // new bullet
      Bullet newBullet = new Bullet(2, Color.pink, new Posn(250, 300), new Posn(0, -8), 0);
      return new NBullets(new ConsLoBullet(newBullet, this.bullets), this.ships,
          this.bulletsLeft - 1, this.shipsDestroyed, this.ticksSinceLastSpawn, this.rand);
    } else {
      return this;
    }
  }

  // world ends when all bullets are off screen and no more ammo
  public WorldEnd worldEnds() {
    if (this.bulletsLeft <= 0 && this.bullets instanceof MtLoBullet) {
      return new WorldEnd(true, this.finalScene());
    } else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // display game over
  WorldScene finalScene() {
    WorldImage gameOver = new TextImage("Game Over", 24, Color.black);
    return this.displayInfo(this.getEmptyScene().placeImageXY(gameOver, 250, 150));
  }

}

//represents a list of bullets in the game
interface ILoBullet {
  // updates positions for this list of bullets
  ILoBullet updatePos();

  // removes bullets from the list that are off screen
  ILoBullet removeOffscreen();

  // is given ship touching any of the bullets in this list?
  boolean isShipTouching(Ship s);

  // explodes bullets that are touching any ships
  ILoBullet explodeBullets(ILoShip ships);

  // draws all bullets on given scene
  WorldScene draw(WorldScene scene);
}

//represents an empty list of bullets in the game
class MtLoBullet implements ILoBullet {

  /*
   * TEMPLATE:
   * Methods:
   * ...this.updatePos()... - ILoBullet
   * ...this.removeOffscreen()... - ILoBullet
   * ...this.isShipTouching(Ship)... - boolean
   * ...this.explodeBullets(ILoShip)... - ILoBullet
   * ...this.draw(WorldScene)... - WorldScene
   */

  // updates positions for this list of bullets
  public ILoBullet updatePos() {
    return this;
  }

  // remove off screen bullets
  public ILoBullet removeOffscreen() {
    return this;
  }

  // is given ship touching any of the bullets in this list?
  public boolean isShipTouching(Ship s) {
    return false;
  }

  // explodes bullets that are touching any ships
  public ILoBullet explodeBullets(ILoShip ships) {
    return this;
  }

  // draws all bullets on given scene
  public WorldScene draw(WorldScene scene) {
    return scene;
  }
}

//represents a non-empty list of bullets in the game
class ConsLoBullet implements ILoBullet {
  Bullet first;
  ILoBullet rest;

  ConsLoBullet(Bullet first, ILoBullet rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ...this.first... - Bullet
   * ...this.rest... - ILoBullet
   *
   * Methods:
   * ...this.updatePos()... - ILoBullet
   * ...this.removeOffscreen()... - ILoBullet
   * ...this.isShipTouching(Ship)... - boolean
   * ...this.explodeBullets(ILoShip)... - ILoBullet
   * ...this.draw(WorldScene)... - WorldScene
   */

  // updates positions for this list of bullets
  public ILoBullet updatePos() {
    return new ConsLoBullet(this.first.updatePos(), this.rest.updatePos());
  }

  // remove off screen bullets
  public ILoBullet removeOffscreen() {
    if (this.first.isOffscreen()) {
      return this.rest.removeOffscreen();
    } else {
      return new ConsLoBullet(this.first, this.rest.removeOffscreen());
    }
  }

  // is given ship touching any of the bullets in this list?
  public boolean isShipTouching(Ship s) {
    return this.first.isShipTouching(s) || this.rest.isShipTouching(s);
  }

  // explodes bullets that are touching any ships
  public ILoBullet explodeBullets(ILoShip ships) {
    if (ships.isBulletTouching(this.first)) {
      return this.first.explode(this.rest.explodeBullets(ships));
    } else {
      return new ConsLoBullet(this.first, this.rest.explodeBullets(ships));
    }
  }

  // draws all bullets on given scene
  public WorldScene draw(WorldScene scene) {
    return this.rest.draw(this.first.drawBullet(scene));
  }
}

//represents a bullet in the game
class Bullet {
  int radius;
  Color color;
  Posn pos;
  Posn vel;
  int prevExplosions;

  Bullet(int radius, Color color, Posn pos, Posn vel, int prevExplosions) {
    if (radius > 10) {
      this.radius = 10;
    } else {
      this.radius = radius;
    }
    this.color = color;
    this.pos = pos;
    this.vel = vel;
    this.prevExplosions = prevExplosions;
  }

  /*
   * TEMPATE:
   * Fields:
   * ...this.radius... - int
   * ...this.color... - Color
   * ...this.pos... - Posn
   * ...this.vel... - Posn
   * ...this.int... - prevExplosions
   *
   * Methods:
   * ...this.updatePos()... - ILoBullet
   * ...this.isOffscreen()... - boolean
   * ...this.isShipTouching(Ship)... - boolean
   * ...this.explode(ILoBullet)... - ILoBullet
   * ...this.explodeHelp(ILoBullet, double, double)... - ILoBullet
   * ...this.drawBullet(WorldScene)... - WorldScene
   *
   */

  // updates position for this bullet based on velocity
  Bullet updatePos() {
    return new Bullet(this.radius, this.color, pos.addPosn(this.vel), this.vel,
        this.prevExplosions);
  }

  // is this bullet off screen
  boolean isOffscreen() {
    return this.pos.isOffscreen();
  }

  // is this bullet touching the given ship?
  boolean isShipTouching(Ship s) {
    return this.pos.isWithin(s.pos, this.radius + s.radius);
  }

  // explodes bullet into more bullets
  ILoBullet explode(ILoBullet bullets) {
    // number of bullets to make
    int numBullets = this.prevExplosions + 2;
    // angle between bullets
    double angleInc = Math.PI * 2 / numBullets;
    return explodeHelp(bullets, numBullets, angleInc);
  }

  // helper method. Generates bullets in a circle, incrementing angle by given
  // amount each time
  ILoBullet explodeHelp(ILoBullet bullets, int numBullets, double angleInc) {
    if (numBullets <= 0) {
      return bullets;
    } else {
      Bullet newBullet = new Bullet(this.radius + (this.prevExplosions + 1) * 2, this.color,
          this.pos, new Posn((int) (8 * Math.cos(angleInc * numBullets)),
              (int) (8 * Math.sin(angleInc * numBullets))),
          this.prevExplosions + 1);
      return explodeHelp(new ConsLoBullet(newBullet, bullets), numBullets - 1, angleInc);
    }
  }

  // draws bullet on given scene
  WorldScene drawBullet(WorldScene scene) {
    return this.pos.drawAt(scene, new CircleImage(this.radius, OutlineMode.SOLID, this.color));
  }
}

//represents a list of ships
interface ILoShip {
  // updates position for all ships in this list
  ILoShip updatePos();

  // removes off screen ships from the list
  ILoShip removeOffscreen();

  // remove ships that are currently touching a bullet
  ILoShip explodeShips(ILoBullet bullets);

  // is given bullet touching any of these ships
  boolean isBulletTouching(Bullet b);

  // return number of ships that are touching any bullets
  int shipsDestroyed(ILoBullet bullets);

  // spawn new ships
  ILoShip spawnNewShips(int shipsToSpawn, Random rand);

  // draw all ships on given WorldScene
  WorldScene draw(WorldScene scene);
}

//represents an empty list of ships
class MtLoShip implements ILoShip {

  /*
   * TEMPLATE:
   * Methods:
   * ...this.updatePos()... - ILoShip
   * ...this.removeOffscreen()... - ILoShip
   * ...this.explodeShips(Ship)... - ILoShip
   * ...this.isBulletTouching(Bullet)... - boolean
   * ...this.shipsDestroyed(ILoBullet)... - int
   * ...this.spawnNewShips(int, Random)... - ILoShip
   * ...this.draw(WorldScene)... - WorldScene
   */

  // updates position for ships in the list
  public ILoShip updatePos() {
    return this;
  }

  // removes off screen ships
  public ILoShip removeOffscreen() {
    return this;
  }

  // remove ships that are currently touching a bullet
  public ILoShip explodeShips(ILoBullet bullets) {
    return this;
  }

  // is given bullet touching any of these ships
  public boolean isBulletTouching(Bullet b) {
    return false;
  }

  // return number of ships that are touching any bullets
  public int shipsDestroyed(ILoBullet bullets) {
    return 0;
  }

  public ILoShip spawnNewShips(int shipsToSpawn, Random rand) {
    if (shipsToSpawn <= 0) {
      return this;
    } else {
      int velX;
      int posX;
      int posY;
      // create new ship
      int direction = rand.nextInt(2);
      if (direction == 0) {
        velX = -4;
        posX = 500;
      } else {
        velX = 4;
        posX = 0;
      }
      posY = (int) ((rand.nextInt(5) + 1.5) * 300.0 / 7.0);
      Ship newShip = new Ship(10, Color.CYAN, new Posn(posX, posY), new Posn(velX, 0));
      return new ConsLoShip(newShip, this).spawnNewShips(shipsToSpawn - 1, rand);
    }
  }

  // draw all ships on given WorldScene
  public WorldScene draw(WorldScene scene) {
    return scene;
  }
}

//represents a non-empty list of ships
class ConsLoShip implements ILoShip {
  Ship first;
  ILoShip rest;

  ConsLoShip(Ship first, ILoShip rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ...this.first... - Ship
   * ...this.rest... - ILoShip
   *
   * Methods:
   * ...this.updatePos()... - ILoShip
   * ...this.removeOffscreen()... - ILoShip
   * ...this.explodeShips(Ship)... - ILoShip
   * ...this.isBulletTouching(Bullet)... - boolean
   * ...this.shipsDestroyed(ILoBullet)... - int
   * ...this.spawnNewShips(int, Random)... - ILoShip
   * ...this.draw(WorldScene)... - WorldScene
   */

  // updates position for ships in the list
  public ILoShip updatePos() {
    return new ConsLoShip(this.first.updatePos(), this.rest.updatePos());
  }

  // removes off screen ships from the list
  public ILoShip removeOffscreen() {
    if (this.first.isOffscreen()) {
      return this.rest.removeOffscreen();
    } else {
      return new ConsLoShip(this.first, this.rest.removeOffscreen());
    }
  }

  // remove ships that are currently touching a bullet
  public ILoShip explodeShips(ILoBullet bullets) {
    if (bullets.isShipTouching(this.first)) {
      return this.rest;
    } else {
      return new ConsLoShip(this.first, this.rest.explodeShips(bullets));
    }
  }

  // is given bullet touching any of these ships
  public boolean isBulletTouching(Bullet b) {
    return this.first.isBulletTouching(b) || this.rest.isBulletTouching(b);
  }

  public int shipsDestroyed(ILoBullet bullets) {
    if (bullets.isShipTouching(this.first)) {
      return 1 + this.rest.shipsDestroyed(bullets);
    } else {
      return this.rest.shipsDestroyed(bullets);
    }
  }

  public ILoShip spawnNewShips(int shipsToSpawn, Random rand) {
    if (shipsToSpawn <= 0) {
      return this;
    } else {
      int velX;
      int posX;
      int posY;
      // create new ship
      int direction = rand.nextInt(2);
      if (direction == 0) {
        velX = -4;
        posX = 500;
      } else {
        velX = 4;
        posX = 0;
      }
      posY = (int) ((rand.nextInt(5) + 1.5) * 300.0 / 7.0);
      Ship newShip = new Ship(10, Color.CYAN, new Posn(posX, posY), new Posn(velX, 0));
      return new ConsLoShip(newShip, this).spawnNewShips(shipsToSpawn - 1, rand);
    }
  }

  // draw all ships on given WorldScene
  public WorldScene draw(WorldScene scene) {
    return this.rest.draw(this.first.drawShip(scene));
  }
}

//represents a ship in the game
class Ship {
  int radius;
  Color color;
  Posn pos;
  Posn vel;

  Ship(int radius, Color color, Posn pos, Posn vel) {
    this.radius = radius;
    this.color = color;
    this.pos = pos;
    this.vel = vel;
  }

  /*
   * TEMPATE:
   * Fields:
   * ...this.radius... - int
   * ...this.color... - Color
   * ...this.pos... - Posn
   * ...this.vel... - Posn
   *
   * Methods:
   * ...this.updatePos()... - Ship
   * ...this.removeOffscreen()... - boolean
   * ...this.isBulletTouching(Bullet)... - boolean
   * ...this.drawShip(WorldScene)... - WorldScene
   *
   */

  // updates position of this ship based on its velocity
  Ship updatePos() {
    return new Ship(this.radius, this.color, this.pos.addPosn(this.vel), this.vel);
  }

  // is this ship off screen?
  boolean isOffscreen() {
    return this.pos.isOffscreen();
  }

  // is given bullet touching this ship
  boolean isBulletTouching(Bullet b) {
    return this.pos.isWithin(b.pos, this.radius + b.radius);
  }

  // draws this ship on given scene
  WorldScene drawShip(WorldScene scene) {
    return this.pos.drawAt(scene, new CircleImage(this.radius, OutlineMode.SOLID, this.color));
  }
}

//represents a vector or position
class Posn {
  int x;
  int y;

  Posn(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /*
   * TEMPATE:
   * Fields:
   * ...this.x... - int
   * ...this.y... - int
   *
   * Methods:
   * ...this.addPosn(Posn)... - Ship
   * ...this.isOffscreen()... - boolean
   * ...this.isWithin(Posn, int)... - boolean
   * ...this.drawAt(WorldScene, WorldImage)... - WorldScene
   */

  // adds another posn to this posn
  Posn addPosn(Posn other) {
    return new Posn(this.x + other.x, this.y + other.y);
  }

  // is this posn off screen?
  boolean isOffscreen() {
    return this.x > 500 || this.x < 0 || this.y > 300 || this.y < 0;
  }

  // are these Posns within the given distance of each other
  boolean isWithin(Posn p, int dist) {
    return (int) (Math.hypot((double) (p.x - this.x), (double) (p.y - this.y))) <= dist;
  }

  // draws given image on given scene at position specified by this posn
  WorldScene drawAt(WorldScene scene, WorldImage img) {
    return scene.placeImageXY(img, this.x, this.y);
  }
}

class ExamplesNBullets {
  Random rand = new Random();
  Posn p1 = new Posn(3, 4);
  Posn p2 = new Posn(0, 0);
  Bullet bullet1 = new Bullet(2, Color.PINK, new Posn(10, 7), new Posn(0, 8), 0);
  Bullet bullet2 = new Bullet(10, Color.PINK, new Posn(3, -10), new Posn(0, 0), 4);
  ILoBullet bullets1 = new ConsLoBullet(this.bullet1, new MtLoBullet());
  ILoBullet bullets2 = new ConsLoBullet(this.bullet2, this.bullets1);
  Ship ship1 = new Ship(10, Color.CYAN, new Posn(5, 5), new Posn(-4, 0));
  ILoShip ships1 = new ConsLoShip(this.ship1, new MtLoShip());
  ILoShip ships2 = new ConsLoShip(new Ship(10, Color.CYAN, new Posn(-30, 4), new Posn(0, 0)),
      this.ships1);
  NBullets game = new NBullets(this.bullets1, this.ships1, 2, 2, 5, this.rand);

  boolean testPosnAddPosn(Tester t) {
    return t.checkExpect(this.p1.addPosn(this.p2), new Posn(3, 4))
        && t.checkExpect(this.p1.addPosn(new Posn(5, 4)), new Posn(8, 8));
  }

  boolean testPosnIsOffscreen(Tester t) {
    return t.checkExpect(new Posn(600, 3).isOffscreen(), true)
        && t.checkExpect(this.p1.isOffscreen(), false)
        && t.checkExpect(new Posn(-1, 0).isOffscreen(), true);
  }

  boolean testPosnIsWithin(Tester t) {
    return t.checkExpect(this.p1.isWithin(this.p2, 5), true)
        && t.checkExpect(this.p1.isWithin(this.p1, 3), true)
        && t.checkExpect(this.p1.isWithin(this.p2, 4), false)
        && t.checkExpect(this.p2.isWithin(this.p2, 6), true);
  }

  boolean testPosnDrawAt(Tester t) {
    // example ship image
    WorldImage ship = new CircleImage(10, OutlineMode.SOLID, Color.CYAN);
    return t.checkExpect(this.p1.drawAt(new WorldScene(500, 300), ship),
        new WorldScene(500, 300).placeImageXY(ship, 3, 4));
  }

  boolean testShipUpdatePos(Tester t) {
    return t.checkExpect(this.ship1.updatePos(),
        new Ship(10, Color.CYAN, new Posn(1, 5), new Posn(-4, 0)));
  }

  boolean testShipIsOffscreen(Tester t) {
    return t.checkExpect(new Ship(10, Color.CYAN, new Posn(-3, 2), new Posn(5, 4)).isOffscreen(),
        true) && t.checkExpect(this.ship1.isOffscreen(), false);
  }

  boolean testShipIsBulletTouching(Tester t) {
    return t.checkExpect(this.ship1.isBulletTouching(this.bullet1), true) && t.checkExpect(
        this.ship1.isBulletTouching(new Bullet(2,
            Color.PINK, new Posn(30, 40), new Posn(0, 8), 0)),
        false);
  }

  boolean testShipDrawShip(Tester t) {
    return t.checkExpect(this.ship1.drawShip(new WorldScene(500, 300)), new WorldScene(500, 300)
        .placeImageXY(new CircleImage(10, OutlineMode.SOLID, Color.CYAN), 5, 5));
  }

  boolean testBulletUpdatePos(Tester t) {
    return t.checkExpect(this.bullet1.updatePos(),
        new Bullet(2, Color.PINK, new Posn(10, 15), new Posn(0, 8), 0));
  }

  boolean testBulletisOffscreen(Tester t) {
    return t.checkExpect(this.bullet1.isOffscreen(), false) && t.checkExpect(
        new Bullet(2, Color.PINK, new Posn(-100, 2), new Posn(0, 0), 0).isOffscreen(), true);
  }

  boolean testBulletIsShipTouching(Tester t) {
    return t.checkExpect(this.bullet1.isShipTouching(this.ship1), true) && t.checkExpect(
        new Bullet(2, Color.PINK, new Posn(-100, 2), new Posn(0, 0), 0).isShipTouching(this.ship1),
        false);
  }

  boolean testBulletExplode(Tester t) {
    return t.checkExpect(this.bullet1.explode(new MtLoBullet()),
        new ConsLoBullet(new Bullet(4, Color.PINK, new Posn(10, 7), new Posn(-8, 0), 1),
            new ConsLoBullet(new Bullet(4, Color.PINK, new Posn(10, 7), new Posn(8, 0), 1),
                new MtLoBullet())));
  }

  boolean testBulletExplodeHelp(Tester t) {
    return t.checkExpect(this.bullet1.explodeHelp(new MtLoBullet(), 1, Math.PI), new ConsLoBullet(
        new Bullet(4, Color.PINK, new Posn(10, 7), new Posn(-8, 0), 1), new MtLoBullet()));
  }

  boolean testBulletDrawBullet(Tester t) {
    return t.checkExpect(this.bullet1.drawBullet(new WorldScene(500, 300)), new WorldScene(500, 300)
        .placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.PINK), 10, 7));
  }

  boolean testILoShipUpdatePos(Tester t) {
    return t.checkExpect(new MtLoShip(), new MtLoShip())
        && t.checkExpect(this.ships1.updatePos(),
            new ConsLoShip(new Ship(10, Color.CYAN, new Posn(1, 5), new Posn(-4, 0)),
                new MtLoShip()))
        && t.checkExpect(this.ships2.updatePos(),
            new ConsLoShip(new Ship(10, Color.CYAN, new Posn(-30, 4), new Posn(0, 0)),
                new ConsLoShip(new Ship(10, Color.CYAN, new Posn(1, 5), new Posn(-4, 0)),
                    new MtLoShip())));
  }

  boolean testILoShipRemoveOffscreen(Tester t) {
    return t.checkExpect(new MtLoShip(), new MtLoShip())
        && t.checkExpect(this.ships2.removeOffscreen(), this.ships1);
  }

  boolean testILoShipExplodeShips(Tester t) {
    return t.checkExpect(new MtLoShip().explodeShips(this.bullets1), new MtLoShip())
        && t.checkExpect(this.ships2.explodeShips(this.bullets1), new ConsLoShip(
            new Ship(10, Color.CYAN, new Posn(-30, 4), new Posn(0, 0)), new MtLoShip()));
  }

  boolean testILoShipIsBulletTouching(Tester t) {
    return t.checkExpect(new MtLoShip().isBulletTouching(this.bullet1), false)
        && t.checkExpect(this.ships2.isBulletTouching(this.bullet1), true);
  }

  boolean testILoShipShipsDestroyed(Tester t) {
    return t.checkExpect(new MtLoShip().shipsDestroyed(this.bullets1), 0)
        && t.checkExpect(this.ships2.shipsDestroyed(this.bullets1), 1);
  }

  boolean testILoShipSpawnNewShips(Tester t) {
    Random rand = new Random(5);
    rand.nextInt(2);
    return t.checkExpect(new MtLoShip().spawnNewShips(0, rand), new MtLoShip())
        && t.checkExpect(new MtLoShip().spawnNewShips(1, rand),
            new ConsLoShip(
                new Ship(10, Color.CYAN,
                    new Posn(500, (int) ((rand.nextInt(5) + 1.5) * 300.0 / 7.0)), new Posn(-4, 0)),
                new MtLoShip()));
  }

  boolean testILoShipDraw(Tester t) {
    return t.checkExpect(new MtLoShip().draw(new WorldScene(500, 300)), new WorldScene(500, 300))
        && t.checkExpect(this.ships1.draw(new WorldScene(500, 300)), new WorldScene(500, 300)
            .placeImageXY(new CircleImage(10, OutlineMode.SOLID, Color.CYAN), 5, 5));
  }

  boolean testILoBulletUpdatePos(Tester t) {
    return t.checkExpect(new MtLoBullet().updatePos(), new MtLoBullet())
        && t.checkExpect(this.bullets1.updatePos(), new ConsLoBullet(
            new Bullet(2, Color.PINK, new Posn(10, 15), new Posn(0, 8), 0), new MtLoBullet()));
  }

  boolean testILoBulletRemoveOffscreen(Tester t) {
    return t.checkExpect(new MtLoBullet().removeOffscreen(), new MtLoBullet())
        && t.checkExpect(this.bullets2.removeOffscreen(), this.bullets1);
  }

  boolean testILoBulletIsShipTouching(Tester t) {
    return t.checkExpect(new MtLoBullet().isShipTouching(this.ship1), false)
        && t.checkExpect(this.bullets2.isShipTouching(this.ship1), true)
        && t.checkExpect(new ConsLoBullet(this.bullet2, new MtLoBullet())
            .isShipTouching(new Ship(10, Color.CYAN, new Posn(-500, -500), new Posn(0, 0))), false);
  }

  boolean testILoBulletExplodeBullets(Tester t) {
    return t.checkExpect(new MtLoBullet().explodeBullets(this.ships1), new MtLoBullet())
        && t.checkExpect(this.bullets1.explodeBullets(this.ships1),
            new ConsLoBullet(new Bullet(4, Color.PINK, new Posn(10, 7), new Posn(-8, 0), 1),
                new ConsLoBullet(new Bullet(4, Color.PINK, new Posn(10, 7), new Posn(8, 0), 1),
                    new MtLoBullet())));
  }

  boolean testILoBulletDraw(Tester t) {
    return t.checkExpect(new MtLoBullet().draw(new WorldScene(500, 300)), new WorldScene(500, 300))
        && t.checkExpect(this.bullets1.draw(new WorldScene(500, 300)), new WorldScene(500, 300)
            .placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.PINK), 10, 7));
  }

  boolean testMakeScene(Tester t) {
    return t.checkExpect(this.game.makeScene(),
        new WorldScene(0, 0)
            .placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.PINK), 10, 7)
            .placeImageXY(new CircleImage(10, OutlineMode.SOLID, Color.CYAN), 5, 5).placeImageXY(
                new TextImage("bullets left: 2; ships destroyed: 2", 13, Color.BLACK), 100, 290));
  }

  boolean testDisplayInfo(Tester t) {
    return t.checkExpect(this.game.displayInfo(new WorldScene(500, 300)),
        new WorldScene(500, 300).placeImageXY(
            new TextImage("bullets left: 2; ships destroyed: 2", 13, Color.BLACK), 100, 290));
  }

  boolean testOnTick(Tester t) {
    return t.checkExpect(this.game.onTick(),
        new NBullets(new ConsLoBullet(
            new Bullet(2, Color.PINK, new Posn(10, 15), new Posn(0, 8), 0),
                new MtLoBullet()), new ConsLoShip(
                    new Ship(10, Color.CYAN, new Posn(1, 5), new Posn(-4, 0)),
                    new MtLoShip()), 2, 2, 6, this.rand));
  }

  boolean testUpdatePos(Tester t) {
    return t.checkExpect(this.game.updatePos(),
        new NBullets(new ConsLoBullet(
            new Bullet(2, Color.PINK, new Posn(10, 15), new Posn(0, 8), 0),
                new MtLoBullet()), new ConsLoShip(new Ship(10,
                    Color.CYAN, new Posn(1, 5), new Posn(-4, 0)),
                    new MtLoShip()), 2, 2, 5, this.rand));
  }

  boolean testRemoveOffscreen(Tester t) {
    return t.checkExpect(
        new NBullets(this.bullets2, this.ships2, 2, 2, 5, this.rand).removeOffscreen(),
        this.game);
  }

  boolean testExplosions(Tester t) {
    return t.checkExpect(this.game.explosions(),
        new NBullets(new ConsLoBullet(new Bullet(4,
            Color.PINK, new Posn(10, 7), new Posn(-8, 0), 1),
            new ConsLoBullet(new Bullet(4, Color.PINK, new Posn(10, 7), new Posn(8, 0), 1),
                new MtLoBullet())), new MtLoShip(), 2, 3, 5, this.rand));
  }

  boolean testSpawnShips(Tester t) {
    return t.checkExpect(this.game.spawnShips(),
        new NBullets(this.bullets1, this.ships1, 2, 2, 6, this.rand))
        && t.checkExpect(new NBullets(new MtLoBullet(),
            new MtLoShip(), 2, 2, 28, new Random(7)).spawnShips(),
            new NBullets(new MtLoBullet(), new ConsLoShip(
                new Ship(10, Color.CYAN, new Posn(500, 64), new Posn(-4, 0)),
                new ConsLoShip(new Ship(10, Color.CYAN, new Posn(0, 64), new Posn(4, 0)),
                    new MtLoShip())), 2, 2, 0, this.rand));
  }

  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.game.onKeyEvent(" "),
        new NBullets(new ConsLoBullet(new Bullet(
            2, Color.PINK, new Posn(250, 300), new Posn(0, -8), 0),
            this.bullets1), this.ships1, 1, 2, 5, this.rand))
        && t.checkExpect(this.game.onKeyEvent("not a space"), this.game)
        && t.checkExpect(new NBullets(this.bullets1, this.ships1,
            0, 12, 19, this.rand), new NBullets(this.bullets1, this.ships1,
                0, 12, 19, this.rand));
  }

  boolean testWorldEnds(Tester t) {
    return t.checkExpect(this.game.worldEnds(), new WorldEnd(false, this.game.makeScene()))
        && t.checkExpect(new NBullets(this.bullets1, this.ships1, 0, 3, 6, this.rand).worldEnds(),
            new WorldEnd(false, new NBullets(this.bullets1,
                this.ships1, 0, 3, 6, this.rand).makeScene()))
        && t.checkExpect(new NBullets(new MtLoBullet(),
            this.ships1, 0, 3, 5).worldEnds(), new WorldEnd(true, this.game.finalScene()));
  }

  boolean testFinalScene(Tester t) {
    return t.checkExpect(this.game.finalScene(),
        new WorldScene(0, 0).placeImageXY(
            new TextImage("Game Over", 24, Color.black), 250, 150)
        .placeImageXY(new TextImage("bullets left: 2; ships destroyed: 2",
            13, Color.BLACK), 100, 290));
  }

  boolean testBigBang(Tester t) {
    NBullets w = new NBullets(5);
    int worldWidth = 500;
    int worldHeight = 300;
    double tickRate = 1.0 / 28.0;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }
}