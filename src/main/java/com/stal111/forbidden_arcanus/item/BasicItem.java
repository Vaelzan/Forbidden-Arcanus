package com.stal111.forbidden_arcanus.item;

import com.stal111.forbidden_arcanus.Main;

import net.minecraft.item.Item;

public class BasicItem extends Item {

	public BasicItem(String name) {
		super(new Item.Properties().group(Main.FORBIDDEN_ARCANUS));
		this.setRegistryName(Main.MODID, name);
	}
	
	
	public BasicItem(String name, Item.Properties properties) {
		super(properties.group(Main.FORBIDDEN_ARCANUS));
		this.setRegistryName(Main.MODID, name);
	}
	
}