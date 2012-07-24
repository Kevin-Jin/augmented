package op_lando.map.entity;

public interface AuxiliaryEntity<E extends Enum<E>> extends DrawableEntity {
	E getType();

	void setFlip(boolean flip);
}
