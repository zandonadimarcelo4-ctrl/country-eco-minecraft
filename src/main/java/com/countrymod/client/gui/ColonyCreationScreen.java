package com.countrymod.client.gui;

import com.countrymod.model.ColonyType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.gui.DrawContext;

/**
 * GUI screen for creating a new colony.
 * Allows selection of colony name and type.
 */
@SuppressWarnings("unused")
public class ColonyCreationScreen extends Screen {
	private final BlockPos flagPos;
	private TextFieldWidget colonyNameField;
	private ColonyType selectedType = ColonyType.SETTLEMENT;
	private ButtonWidget createButton;
	
	public ColonyCreationScreen(BlockPos flagPos) {
		super(Text.literal("Establish Colony"));
		this.flagPos = flagPos;
	}
	
	@Override
	protected void init() {
		int centerX = this.width / 2;
		int centerY = this.height / 2;
		
		// Colony name input field
		colonyNameField = new TextFieldWidget(
			this.textRenderer,
			centerX - 100,
			centerY - 60,
			200,
			20,
			Text.literal("Colony Name")
		);
		colonyNameField.setMaxLength(32);
		colonyNameField.setPlaceholder(Text.literal("Enter colony name..."));
		addSelectableChild(colonyNameField);
		
		// Colony type buttons
		int buttonY = centerY - 20;
		for (ColonyType type : ColonyType.values()) {
			ButtonWidget button = ButtonWidget.builder(
				Text.literal(type.getDisplayName()),
				btn -> selectType(type)
			).dimensions(centerX - 100 + (type.ordinal() * 105), buttonY, 100, 20)
			.build();
			addDrawableChild(button);
		}
		
		// Create button
		createButton = ButtonWidget.builder(
			Text.literal("Establish Colony"),
			btn -> createColony()
		).dimensions(centerX - 100, centerY + 60, 200, 20)
		.build();
		addDrawableChild(createButton);
		
		// Cancel button
		ButtonWidget cancelButton = ButtonWidget.builder(
			Text.literal("Cancel"),
			btn -> close()
		).dimensions(centerX - 100, centerY + 85, 200, 20)
		.build();
		addDrawableChild(cancelButton);
	}
	
	private void selectType(ColonyType type) {
		this.selectedType = type;
	}
	
	private void createColony() {
		String colonyName = colonyNameField.getText().trim();
		if (colonyName.isEmpty()) {
			return;
		}
		
		// TODO: Send packet to server to create colony
		// This will be implemented with networking
		
		close();
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		
		// Title
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
		
		// Instructions
		context.drawCenteredTextWithShadow(this.textRenderer, 
			Text.literal("Choose colony type:"), 
			this.width / 2, this.height / 2 - 40, 0xAAAAAA);
		
		// Render text field
		colonyNameField.render(context, mouseX, mouseY, delta);
		
		// Selected type indicator
		context.drawCenteredTextWithShadow(this.textRenderer, 
			Text.literal("Selected: " + selectedType.getDisplayName()), 
			this.width / 2, this.height / 2 + 40, 0x00FF00);
	}
	
	@Override
	public boolean shouldPause() {
		return false;
	}
}
