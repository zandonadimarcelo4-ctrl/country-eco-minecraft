package com.countrymod.client.gui;

import com.countrymod.model.GovernmentType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.gui.DrawContext;

/**
 * GUI screen for creating a new country.
 * Allows selection of country name and government type.
 */
@SuppressWarnings("unused")
public class CountryCreationScreen extends Screen {
	private final BlockPos flagPos;
	private TextFieldWidget countryNameField;
	private GovernmentType selectedGovernment = GovernmentType.REPUBLIC;
	private ButtonWidget createButton;
	
	public CountryCreationScreen(BlockPos flagPos) {
		super(Text.literal("Create Your Country"));
		this.flagPos = flagPos;
	}
	
	@Override
	protected void init() {
		int centerX = this.width / 2;
		int centerY = this.height / 2;
		
		// Country name input field
		countryNameField = new TextFieldWidget(
			this.textRenderer,
			centerX - 100,
			centerY - 60,
			200,
			20,
			Text.literal("Country Name")
		);
		countryNameField.setMaxLength(32);
		countryNameField.setPlaceholder(Text.literal("Enter country name..."));
		addSelectableChild(countryNameField);
		
		// Government type buttons
		int buttonY = centerY - 20;
		for (GovernmentType type : GovernmentType.values()) {
			ButtonWidget button = ButtonWidget.builder(
				Text.literal(type.getDisplayName()),
				btn -> selectGovernment(type)
			).dimensions(centerX - 100 + (type.ordinal() % 2) * 105, 
			             buttonY + (type.ordinal() / 2) * 25, 
			             100, 
			             20)
			.build();
			addDrawableChild(button);
		}
		
		// Create button
		createButton = ButtonWidget.builder(
			Text.literal("Create Country"),
			btn -> createCountry()
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
	
	private void selectGovernment(GovernmentType type) {
		this.selectedGovernment = type;
	}
	
	private void createCountry() {
		String countryName = countryNameField.getText().trim();
		if (countryName.isEmpty()) {
			return;
		}
		
		// TODO: Send packet to server to create country
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
			Text.literal("Choose your government type:"), 
			this.width / 2, this.height / 2 - 40, 0xAAAAAA);
		
		// Render text field
		countryNameField.render(context, mouseX, mouseY, delta);
		
		// Selected government indicator
		context.drawCenteredTextWithShadow(this.textRenderer, 
			Text.literal("Selected: " + selectedGovernment.getDisplayName()), 
			this.width / 2, this.height / 2 + 40, 0x00FF00);
	}
	
	@Override
	public boolean shouldPause() {
		return false;
	}
}
