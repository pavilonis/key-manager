package lt.pavilonis.keymanager.ui.scanlog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;

public class UserCreationForm extends BorderPane {

   private static final int SPACING = 10;
   private static final Font DEFAULT_FONT = Font.font(null, FontWeight.SEMI_BOLD, 18);
   private final MessageSourceAdapter messages = Spring.getBean(MessageSourceAdapter.class);
   private final TextField fieldName = new TextField();
   private final TextField fieldRole = new TextField();
   private final TextField fieldGroup = new TextField();
   private final Button buttonConfirm = new Button(messages.get("confirm"));
   private final Button buttonCancel = new Button(messages.get("cancel"));
   private final String cardCode;
   private Runnable closeAction;

   public UserCreationForm(String cardCode) {
      this.cardCode = cardCode;
      setPadding(new Insets(100));
      setStyle("-fx-background-color: #D5D5D5;");

      setTop(createTitle());
      Pane fields = createFields();
      setCenter(fields);
      setAlignment(fields, Pos.CENTER);
      setBottom(createButtons());
   }

   private Pane createFields() {
      fieldName.setFont(DEFAULT_FONT);
      fieldRole.setFont(DEFAULT_FONT);
      fieldGroup.setFont(DEFAULT_FONT);

      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(SPACING);
      grid.setVgap(SPACING);
      grid.addRow(0, label("name"), fieldName);
      grid.addRow(1, label("role"), fieldRole);
      grid.addRow(2, label("group"), fieldGroup);

      return grid;
   }

   private HBox createButtons() {
      buttonConfirm.setFont(DEFAULT_FONT);
      buttonCancel.setFont(DEFAULT_FONT);
      buttonCancel.setCancelButton(true);
      HBox buttons = new HBox(buttonConfirm, buttonCancel);
      buttons.setSpacing(SPACING);
      buttons.setAlignment(Pos.CENTER);
      return buttons;
   }

   private HBox createTitle() {
      var titleText = new Text(messages.get("NotificationDisplay.unknownUser"));
      titleText.setFont(Font.font(26));

      var cardCodeText = new Text(cardCode);
      cardCodeText.setFont(Font.font(null, FontWeight.BOLD, 26));

      var fieldWithTitle = new HBox(titleText, cardCodeText);
      fieldWithTitle.setSpacing(SPACING);
      fieldWithTitle.setAlignment(Pos.CENTER);
      return fieldWithTitle;
   }

   private Text label(String name) {
      Text fieldName = new Text(messages.get(name));
      fieldName.setFont(DEFAULT_FONT);
      return fieldName;
   }

   UserRepresentation getValue() {
      return new UserRepresentation(
            fieldName.getText(),
            cardCode,
            fieldRole.getText(),
            fieldGroup.getText()
      );
   }

   public void close() {
      closeAction.run();
   }

   public void setConfirmAction(Runnable confirmAction) {
      buttonConfirm.setOnMouseClicked(click -> confirmAction.run());
   }

   public void setCloseAction(Runnable closeAction) {
      this.closeAction = closeAction;
      this.buttonCancel.setOnMouseClicked(click -> closeAction.run());
   }
}
