package ar.edu.itba.ati.idp.ui.controller.pane.tp3;

import static ar.edu.itba.ati.idp.ui.Constants.CONSTANTS;

import ar.edu.itba.ati.idp.function.ColorOverUniquePixelsBandOperator;
import ar.edu.itba.ati.idp.io.ImageLoader;
import ar.edu.itba.ati.idp.model.ImageFile;
import ar.edu.itba.ati.idp.model.ImageMatrix;
import ar.edu.itba.ati.idp.ui.component.Showable;
import ar.edu.itba.ati.idp.ui.controller.Workspace;
import ar.edu.itba.ati.idp.utils.ResourceLoader;
import java.io.File;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ActiveContoursMethodUI extends HBox implements Showable {
  private static final String LAYOUT_PATH = "ui/pane/tp3/activeContoursMethodUIPane.fxml";
  private static final String STAGE_TITLE = "Active Contours Method";
  private static final Pattern SEQ_FILE_NAME_PATTERN = Pattern.compile("(\\D*)(\\d*)(\\..*)");
  private static final long MILLIS_TO_NEXT_FRAME = 42; // 24 FPS, as 1 sec = 1000 => 1000/24 ~= 42

  private final Stage stage;
  private final ColorOverUniquePixelsBandOperator method;
  private final Deque<ImageMatrix> prevImageMatrices;
  private final Deque<ImageMatrix> nextImageMatrices;
  private final AnimationTimer animationTimer;
  private final Map<KeyCombination, Button> keyCombinationButtonMap;

  @FXML
  private Button startButton;

  @FXML
  private Button prevButton;

  @FXML
  private Button nextButton;

  @FXML
  private Button playPauseButton;

  @FXML
  private Button endButton;

  private Workspace originalWorkspace;
  private Workspace ownWorkspace;
  private ImageMatrix currImageMatrix;
  private boolean isPlayOn;
  private long lastTimePlayed;

  private ActiveContoursMethodUI() {
    ResourceLoader.INSTANCE.loadCustomFxml(LAYOUT_PATH, this);
    this.stage = newState();
    this.method = m -> new double[][][] {m, m, m}; // FIXME: replace with real method call
    this.prevImageMatrices = new LinkedList<>();
    this.nextImageMatrices = new LinkedList<>();
    this.animationTimer = newAnimationTimer();
    this.keyCombinationButtonMap = setKeyCombinationButtonMap();
  }

  private Stage newState() {
    final Stage stage = new Stage(StageStyle.UTILITY);
    stage.setScene(new Scene(this));
    stage.setResizable(false);
    stage.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    return stage;
  }

  private AnimationTimer newAnimationTimer() {
    return new AnimationTimer() {
      @Override
      public void handle(final long now) { // now is in nanoseconds.
        final long nowMillis = TimeUnit.NANOSECONDS.toMillis(now);
        final long elapsedMs = nowMillis - lastTimePlayed;
        if (elapsedMs < MILLIS_TO_NEXT_FRAME) {
          return;
        }
        lastTimePlayed = nowMillis;

        // Play the next frame, if any.
        if (nextButton.isDisabled()) {
          playPauseButton.fire(); // Stop the animation, as there are no more frames.
        } else {
          nextButton.fire(); // Play the next frame.
        }
      }
    };
  }

  private Map<KeyCombination, Button> setKeyCombinationButtonMap() {
    final Map<KeyCombination, Button> map = new HashMap<>();
    map.put(CONSTANTS.getRightArrowKey(), nextButton);
    map.put(CONSTANTS.getLeftArrowKey(), prevButton);
    map.put(CONSTANTS.getSpaceKey(), playPauseButton);
    map.put(CONSTANTS.getHomeKey(), startButton);
    map.put(CONSTANTS.getEndKey(), endButton);
    return map;
  }
  private void handleKeyPressed(final KeyEvent keyEvent) {
    for (final Entry<KeyCombination, Button> pair : keyCombinationButtonMap.entrySet()) {
      if (pair.getKey().match(keyEvent)) {
        final Button button = pair.getValue();
        if (!button.isDisabled()) {
          button.fire();
        }
        keyEvent.consume(); // Stop passing the event to next node.
        return; // There won't be other key matching this event => we've finished.
      }
    }
  }

  public static ActiveContoursMethodUI newInstance(final Workspace workspace) {
    final ActiveContoursMethodUI activeContoursMethodUI = new ActiveContoursMethodUI();
    activeContoursMethodUI.setWorkspace(workspace);
    return activeContoursMethodUI;
  }


  @Override
  public void show(final String imageName) {
    if (originalWorkspace == null) return;
    originalWorkspace.getOpImageFile().ifPresent(imageFile -> {
      populateData(imageFile);
      configureHandlers();
      showWindow(imageName);
    });
  }

  private void populateData(final ImageFile imageFile) {
    // Assume not in root for simplicity's sake.
    final String parentPathString = imageFile.getFile().getParentFile().getPath();
    final String imageName = imageFile.getFile().getName();
    final Matcher matcher = SEQ_FILE_NAME_PATTERN.matcher(imageName);

    final String[] seqFileName = new String[3]; // string + number + extension

    if (!matcher.find()) {
      throw new IllegalStateException("Illegal format for file. Expected: string + number + extension");
    }

    for (int i = 0; i < seqFileName.length; i++) {
      seqFileName[i] = matcher.group(i + 1);
    }

    final String fileName = seqFileName[0];
    final String frameNumberString = seqFileName[1];
    final String extension = seqFileName[2];

    // Bang if not an int (it MUST be an int as it has been captured by the regex).
    final int initFrameNumber = Integer.parseInt(frameNumberString);

    // Just in case we need to add zeros on the left to find the given file.
    final boolean needZeros = frameNumberString.length() - String.valueOf(initFrameNumber).length() != 0;
    final String format = needZeros ? "%0" + frameNumberString.length() + "d" : "%d";

    // Create an own workspace with this imageFile file (assumed not modified).
    ownWorkspace = newOwnWorkspace(imageFile.getFile());
    // Analyze the current image.
    currImageMatrix = ownWorkspace.applyToImage(method).getImageMatrix();

    // Curr frame is assumed to be the first one (i.e, no previous frames is assumed).
    // Empty both prev&next frames just in case...
    prevImageMatrices.clear();
    nextImageMatrices.clear();

    // Find next frames
    int currFrameNumber = initFrameNumber + 1;
    boolean fileExists = true;
    while (fileExists) {
      final String currFileName = fileName + String.format(format, currFrameNumber ++) + extension;
      final File file = new File(parentPathString, currFileName);
      if (!file.exists()) {
        fileExists = false;
        continue;
      }

      nextImageMatrices.offer(loadImageMatrix(file).apply(method));
    }

    // We are done :D
  }

  private void configureHandlers() {
    // Oh, you nasty code :P
    prevButton.setDisable(prevImageMatrices.isEmpty());
    prevButton.setOnAction(event -> swapImageMatrix(prevImageMatrices, nextImageMatrices, prevButton, nextButton));
    nextButton.setDisable(nextImageMatrices.isEmpty());
    nextButton.setOnAction(event -> swapImageMatrix(nextImageMatrices, prevImageMatrices, nextButton, prevButton));
    playPauseButton.setOnAction(event -> {
      if (isPlayOn) {
        animationTimer.stop();
        playPauseButton.setText("Play");
      } else {
        animationTimer.start();
        playPauseButton.setText("Pause");
      }
      isPlayOn = !isPlayOn;
    });
    startButton.setOnAction(event -> completeSwap(prevImageMatrices, nextImageMatrices, prevButton, nextButton));
    endButton.setOnAction(event -> completeSwap(nextImageMatrices, prevImageMatrices, nextButton, prevButton));
  }

  private void completeSwap(final Deque<ImageMatrix> loadDeque,
                            final Deque<ImageMatrix> storeDeque,
                            final Button loadDequeButton,
                            final Button storeDequeButton) {
    if (isPlayOn) {
      playPauseButton.fire(); // Stop the animation first.
    }
    while (!loadDeque.isEmpty()) {
      storeDeque.push(currImageMatrix);
      currImageMatrix = loadDeque.pop();
    }
    ownWorkspace.updateImage(currImageMatrix);
    loadDequeButton.setDisable(loadDeque.isEmpty());
    storeDequeButton.setDisable(storeDeque.isEmpty());
  }

  private void swapImageMatrix(final Deque<ImageMatrix> loadDeque,
                               final Deque<ImageMatrix> storeDeque,
                               final Button loadDequeButton,
                               final Button storeDequeButton) {
    if (!loadDeque.isEmpty()) {
      storeDeque.push(currImageMatrix);
      storeDequeButton.setDisable(false); // At least one element
      currImageMatrix = loadDeque.pop();
      ownWorkspace.updateImage(currImageMatrix);
      if (loadDeque.isEmpty()) { // No more elements
        loadDequeButton.setDisable(true);
      }
    } // If empty, this button should not be enabled, but just in case...
  }

  private Workspace newOwnWorkspace(final File file) {
    final Workspace newOwnWorkspace = Workspace.newInstance();
    if (newOwnWorkspace == null) {
      throw new IllegalStateException("Could not create a new workspace. Aborting...");
    }
    newOwnWorkspace.loadImages(Collections.singletonList(file));
    return newOwnWorkspace;
  }

  private ImageMatrix loadImageMatrix(final File file) {
    return ImageLoader.load(Collections.singletonList(file)).get(0).getImageMatrix();
  }

  private void showWindow(final String imageName) {
    stage.setTitle(STAGE_TITLE + " - " + imageName);
    stage.show(); // This should be before positioning the floating window.
    // Set floating at the center-bottom side of the screen
    final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    stage.setX(bounds.getMinX() + (bounds.getWidth() - this.stage.getWidth()) / 2);
    stage.setY(bounds.getMinY() + (bounds.getHeight() - this.stage.getHeight()));
  }

  @Override
  public void setWorkspace(final Workspace workspace) {
    this.originalWorkspace = workspace;
  }
}
