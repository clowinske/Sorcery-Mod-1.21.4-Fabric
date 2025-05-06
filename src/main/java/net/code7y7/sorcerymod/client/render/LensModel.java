package net.code7y7.sorcerymod.client.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class LensModel extends Model {
    public LensModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
    }
}
