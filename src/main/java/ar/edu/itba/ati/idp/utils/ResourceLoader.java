package ar.edu.itba.ati.idp.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ResourceLoader {
  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLoader.class);
  private static final String LOAD_FXML_ERROR_MSG = "Cannot load the given FXML resource: {}.";

  /**
   * Load the resource with the given path, independently of the environment
   * of execution (development or distribution (e.g., jar)).
   * @param resourcePath The path of the resource to be loaded.
   * @return The URL of the resource with the given path (not null).
   *
   * @throws NullPointerException if the resource with the given path is not found.
   */
  public URL load(final String resourcePath) {
    return Objects.requireNonNull(getClass().getClassLoader().getResource(resourcePath));
  }

  /**
   * Load a custom fxml element on the given {@code fxmlFilePath} layout resource.
   *
   * @param fxmlFilePath The associated layout file of the custom element.
   * @param customFxmlElement The custom element to be set
   *                          as root & controller of the loaded layout.
   */
  public void loadCustomFxml(final String fxmlFilePath, final Object customFxmlElement) {
    final FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.INSTANCE.load(fxmlFilePath));
    fxmlLoader.setRoot(customFxmlElement);
    fxmlLoader.setController(customFxmlElement);

    try {
      fxmlLoader.load();
    } catch (final IOException e) {
      LOGGER.error(LOAD_FXML_ERROR_MSG, fxmlFilePath, e);
    }
  }
}
