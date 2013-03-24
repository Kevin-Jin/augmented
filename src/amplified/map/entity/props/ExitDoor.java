package amplified.map.entity.props;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import amplified.map.CollidableDrawable;
import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.CollisionInformation;
import amplified.map.collisions.Polygon;
import amplified.map.entity.SimpleEntity;
import amplified.map.entity.player.AvatarBody;
import amplified.map.physicquantity.Position;
import amplified.resources.TextureCache;

public class ExitDoor extends SimpleEntity{
	
	private boolean hit;

	public ExitDoor(Position pos) {
		super(new BoundingPolygon(new Polygon[] {
				new Polygon(new Vector2f[] { 
						new Vector2f(0,0),new Vector2f(201,0),new Vector2f(201,258),new Vector2f(0,258)
				}) 
		}), null);
		setPosition(pos);
	}

	@Override
	public void setRotation(float rot) {
		throw new UnsupportedOperationException("Cannot rotate an exit door");
	}
	
	@Override
	public void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables){
		if (collisionInfo.getCollidedWith() instanceof AvatarBody)
			hit = true;
	}

	@Override
	public int getMovabilityIndex() {
		return 0;
	}

	@Override
	public Texture getTexture() {
		 return TextureCache.getTexture(hit ? "openDoor" : "door"); 
	}
	
	public boolean shouldChangeMap(){
		return hit;
	}

}
