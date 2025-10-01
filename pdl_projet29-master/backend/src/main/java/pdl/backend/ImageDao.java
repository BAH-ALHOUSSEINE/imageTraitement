package pdl.backend;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.DataBufferByte;
import java.util.Iterator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.scif.FormatException;
import io.scif.SCIFIO;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import io.scif.img.SCIFIOImgPlus;

import java.io.File;
import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import net.imglib2.RandomAccess;
import java.util.Arrays;
import java.awt.Point;
import java.awt.Toolkit;

import org.apache.logging.log4j.util.ProviderUtil;
import org.apache.tomcat.util.digester.DocumentProperties.Charset;
import org.scijava.io.location.BytesLocation;
//import org.graalvm.compiler.lir.ssa.SSAUtil.PhiValueVisitor;
//import org.graalvm.compiler.lir.ssa.SSAUtil.PhiValueVisitor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import ch.qos.logback.core.Context;
import ij.io.ImageWriter;

@Repository
public class ImageDao implements Dao<Image> {

	private final Map<Long, Image> images = new HashMap<>();

	public ImageDao() throws Exception {
		// placez une image test.jpg dans le dossier "src/main/resources" du projet
		/*
		 * byte [] fileContent1 = Files.readAllBytes(imgFile1.getFile().toPath()); Image
		 * img = new Image("test.jpeg", fileContent1); images.put(img.getId(), img);
		 */

		// File folder = new File("./src/main/resources/images/");
		File folder = new File("/home/kmamadoudram/semestre/projet_logiciel/pdl_projet29/backend/src/main/resources/images");
		if (!folder.isDirectory()) {
			throw new Exception("Le dossier image n'existe pas");
		}
		loadImagesFromDirectory(folder); // on charge tout les images se trouvant dans le dossier images

	}

	@Override
	public Optional<Image> retrieve(final long id) { // prend en parametre un id et retourne l'image correspondant

		return Optional.ofNullable(images.get(id));
	}

	@Override
	public List<Image> retrieveAll() { // permet de recupperer tout les images presentes sur le serveur
		// images.clear();
		List<Image> array = new ArrayList<Image>();
		images.forEach((key, value) -> {
			array.add(value);
		});
		return array;
	}

	@Override
	public void create(final Image img) { // permet d'ajouter l'image passé en parametre au serveur
		Long id = img.getId();
		images.put(id, img);
	}

	@Override
	public void update(final Image img, final String[] params) {
		// Not used
	}

	@Override
	public void delete(final Image img) { // permet de retirer l'image passé en parametre au serveur

		images.remove(img.getId());

	}

	public Image modifierLuminosite(long id, int delta) throws IOException, FormatException { // permet
																								// d'augmenter/duminuer
																								// de <<delta>> la
																								// luminosité de l'image
																								// qui a pour
																								// identifiant le
																								// parametre <<id >>
		// Img<UnsignedByteType> img = images.get(id);
		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> img = ImageConverter.imageFromJPEGBytes(image.getData());
		final Cursor<UnsignedByteType> cursor = img.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			final UnsignedByteType t = cursor.get();
			int new_val = t.get() + delta;
			if (new_val > 255) {
				new_val = 255;
			}
			if (new_val < 0) {
				new_val = 0;
			}
			t.set(new_val);
		}
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(img));

		return image_result;

	}

	public Image couleurImgConversionTogris(long id) throws FormatException, IOException { // permet de convertir
																							// l'image couleur qui a
																							// pour identifiant le
																							// parametre <<id>> en img
																							// en niveau de gris
		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);
		LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel((r, g, b) -> {
			int val = (int) ((r.get() * 0.3 + g.get() * 0.59 + b.get() * 0.11));
			// System.out.println(val);
			r.set(val);
			g.set(val);
			b.set(val);
		});
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(input));

		return image_result;
	}

	public static float fmin(float x, float y, float z) { // renvoi le minimum entre les parametres <<x>> <<y>> <<z>>
		return Math.min((Math.min(x, z)), y);
	}

	public static float fmax(float x, float y, float z) { // renvoi le maximum entre les parametres <<x>> <<y>> <<z>>
		return Math.max(x, (Math.max(y, z)));

	}

	public static void rgbToHsv(int r, int g, int b, float[] hsv) { // permet de convertir les parametres <<r>> <<g>>
																	// <<b>> en <<hsv>>
		float min = fmin(r, g, b);
		float max = fmax(r, g, b);
		if (min == max) {
			hsv[0] = 0;
		} else if (max == r) {
			hsv[0] = (60 * ((g - b) / (max - min)) + 360) % 360;

		} else if (max == g) {
			hsv[0] = 60 * ((b - r) / (max - min)) + 120;
		} else if (max == b) {
			hsv[0] = 60 * ((r - g) / (max - min)) + 240;
		}
		if (max == 0) {
			hsv[1] = 0;
		} else {
			hsv[1] = (1 - (min / max)) * 100;
		}
		hsv[2] = (max / 255) * 100;
		// System.out.println( " Le resultat de la conversion de rgb vers hsv de ("+ ( r
		// +" "+ g +" " + b)+ ") est : (" + ( hsv[0] + " " + hsv[1] + " " +
		// hsv[2])+")");

	}

	public static void hsvToRgb(float h, float s, float v, int[] rgb) { // permet de convertir les parametres <<h>>
																		// <<s>> <<v>> en <<rgb>>
		s = s / 100;
		v = v / 100;
		int hi = (int) ((h / 60)) % 6;
		float f = (h / 60) - hi;
		float l = (v * (1 - s));
		float m = (v * (1 - (f * s)));
		float n = v * ((1 - (1 - f) * s));
		hi = (int) hi;
		if (hi == 0) {
			rgb[0] = Math.round(v * 255);
			rgb[1] = Math.round(n * 255);
			rgb[2] = Math.round(l * 255);

		}
		if (hi == 1) {
			rgb[0] = Math.round(m * 255);
			rgb[1] = Math.round(v * 255);
			rgb[2] = Math.round(l * 255);

		}
		if (hi == 2) {
			rgb[0] = Math.round(l * 255);
			rgb[1] = Math.round(v * 255);
			rgb[2] = Math.round(n * 255);

		}
		if (hi == 3) {
			rgb[0] = Math.round(l * 255);
			rgb[1] = Math.round(m * 255);
			rgb[2] = Math.round(v * 255);

		}
		if (hi == 4) {
			rgb[0] = Math.round(n * 255);
			rgb[1] = Math.round(l * 255);
			rgb[2] = Math.round(v * 255);

		}
		if (hi == 5) {
			rgb[0] = Math.round(v * 255);
			rgb[1] = Math.round(l * 255);
			rgb[2] = Math.round(m * 255);

		}

	}

	public Image filtre(long id, long val) throws FormatException, IOException { // permet de colorer l'image qui a pour
																					// identifiant le parametre <<id>>
																					// avec le parametre <<val>> qui
																					// correspondra a la nouvelle valeur
																					// de teinte de tout les pixels de
																					// l'image
		Image image = images.get(id);
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);
		LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel((r, g, b) -> {
			float hsv[] = new float[3];
			rgbToHsv(r.get(), g.get(), b.get(), hsv);
			hsv[0] = val;
			int rgb[] = new int[3];
			hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
			r.set(rgb[0]);
			g.set(rgb[1]);
			b.set(rgb[2]);
		});
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(input));

		return image_result;

	}

	public Image modifieContrastExtAvecGris(long id) throws FormatException, IOException { // permet de regler le
																							// contraste de l'image qui
																							// a pour identifiant le
																							// parametre <<id>> avec la
																							// methode de l'extension de
																							// la dynamique de l'image
		if (id < 0) {
			return null;
		}
		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		final RandomAccess<UnsignedByteType> r1 = input.randomAccess();
		int min = r1.get().get(); // random value

		int max = r1.get().get(); // random value
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR = inputR.cursor();
		final Cursor<UnsignedByteType> cG = inputG.cursor();
		final Cursor<UnsignedByteType> cB = inputB.cursor();

		while (cR.hasNext() && cG.hasNext() && cB.hasNext()) {
			cR.fwd();
			cG.fwd();
			cB.fwd();
			int val = (int) ((cR.get().get() * 0.3 + cG.get().get() * 0.59 + cB.get().get() * 0.11));
			if (val < min) {
				min = val;
			}
			if (val > max) {
				max = val;
			}

		}

		int[] LUT = new int[256];
		for (int i = 0; i < 256; i++) {
			LUT[i] = (255 * (i - min)) / (max - min);
		}
		final IntervalView<UnsignedByteType> inputR2 = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG2 = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB2 = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR2 = inputR2.cursor();
		final Cursor<UnsignedByteType> cG2 = inputG2.cursor();
		final Cursor<UnsignedByteType> cB2 = inputB2.cursor();
		while (cR2.hasNext() && cG2.hasNext() && cB2.hasNext()) {
			cR2.fwd();
			cG2.fwd();
			cB2.fwd();
			int val_pixel = (int) ((cR2.get().get() * 0.3 + cG2.get().get() * 0.59 + cB2.get().get() * 0.11));
			int new_val = LUT[val_pixel];
			if (new_val > 255) {
				new_val = 255;
			}
			if (new_val < 0) {
				new_val = 0;
			}
			cR2.get().set(new_val);
			cG2.get().set(new_val);
			cB2.get().set(new_val);
		}

		// System.out.println(min +" "+ max);
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(input));

		return image_result;

	}

	public Image modifierContrasteHistAvecGris(long id) throws FormatException, IOException { // permet de regler la
																								// contraste de l'image
																								// qui a pour
																								// identifiant <<id>>
																								// avec la methode
																								// d'egalisation
																								// d'histogramme

		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		int his[] = new int[256];
		for (int i = 0; i < 256; i++) {
			his[i] = 0;
		}
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR = inputR.cursor();
		final Cursor<UnsignedByteType> cG = inputG.cursor();
		final Cursor<UnsignedByteType> cB = inputB.cursor();
		int nb_pixel = 0;
		while (cR.hasNext() && cG.hasNext() && cB.hasNext()) {
			cR.fwd();
			cG.fwd();
			cB.fwd();
			int val = (int) ((cR.get().get() * 0.3 + cG.get().get() * 0.59 + cB.get().get() * 0.11));
			his[val] = his[val] + 1;
			nb_pixel++;

		}
		int his_c[] = new int[256];
		for (int i = 0; i < 0; i++) {
			his_c[i] = 0;
		}

		for (int i = 0; i < 256; i++) {
			for (int j = 0; j <= i; j++) {
				his_c[i] = his_c[i] + his[j];
			}

		}

		final IntervalView<UnsignedByteType> inputR2 = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG2 = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB2 = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR2 = inputR2.cursor();
		final Cursor<UnsignedByteType> cG2 = inputG2.cursor();
		final Cursor<UnsignedByteType> cB2 = inputB2.cursor();
		while (cR2.hasNext() && cG2.hasNext() && cB2.hasNext()) {
			cR2.fwd();
			cG2.fwd();
			cB2.fwd();
			int val_pixel = (int) ((cR2.get().get() * 0.3 + cG2.get().get() * 0.59 + cB2.get().get() * 0.11));
			int new_val = (int) ((his_c[val_pixel] * 255) / nb_pixel);
			if (new_val > 255) {
				new_val = 255;
			}
			if (new_val < 0) {
				new_val = 0;
			}
			cR2.get().set(new_val);
			cG2.get().set(new_val);
			cB2.get().set(new_val);
		}
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(input));

		return image_result;

	}

	public Image modifierContrasteExtAvecCanalV(long id) throws FormatException, IOException { // permet de regler le
																								// contraste de l'image
																								// a pour identifiant
																								// <<id>> en utilisant
																								// le canal V avec la
																								// methode de
																								// l'extension du
																								// dynamique
		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		final RandomAccess<UnsignedByteType> r1 = input.randomAccess();
		int min = r1.get().get(); // random value

		int max = r1.get().get(); // random value
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR = inputR.cursor();
		final Cursor<UnsignedByteType> cG = inputG.cursor();
		final Cursor<UnsignedByteType> cB = inputB.cursor();

		while (cR.hasNext() && cG.hasNext() && cB.hasNext()) {
			cR.fwd();
			cG.fwd();
			cB.fwd();
			float hsv[] = new float[3];
			rgbToHsv(cR.get().get(), cG.get().get(), cB.get().get(), hsv);
			int val = (int) hsv[2];
			if (val < min) {
				min = val;
			}
			if (val > max) {
				max = val;
			}

		}

		int[] tab = new int[256];
		for (int i = 0; i < 256; i++) {
			tab[i] = (255 * (i - min)) / (max - min);
		}
		final IntervalView<UnsignedByteType> inputR2 = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG2 = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB2 = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR2 = inputR2.cursor();
		final Cursor<UnsignedByteType> cG2 = inputG2.cursor();
		final Cursor<UnsignedByteType> cB2 = inputB2.cursor();
		while (cR2.hasNext() && cG2.hasNext() && cB2.hasNext()) {
			cR2.fwd();
			cG2.fwd();
			cB2.fwd();
			float hsv[] = new float[3];
			rgbToHsv(cR2.get().get(), cG2.get().get(), cB2.get().get(), hsv);
			int val_pixel = (int) hsv[2];
			int new_val = tab[val_pixel];
			if (new_val > 255) {
				new_val = 255;
			}
			if (new_val < 0) {
				new_val = 0;
			}
			hsv[2] = new_val;
			int rgb[] = new int[3];
			hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);

			cR2.get().set(rgb[0]);
			cG2.get().set(rgb[1]);
			cB2.get().set(rgb[2]);
		}

		// System.out.println(min +" "+ max);
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(input));

		return image_result;

	}

	public Image modifierContrasteHistAvecCanalV(long id) throws FormatException, IOException {// permet de regler le
																								// contraste de l'image
																								// qui a pour idenfiant
																								// <<id>> en utilisant
																								// la canal V avec la
																								// methode d'egalisation
																								// d'histogramme
		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		int his[] = new int[256];
		for (int i = 0; i < 256; i++) {
			his[i] = 0;
		}
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR = inputR.cursor();
		final Cursor<UnsignedByteType> cG = inputG.cursor();
		final Cursor<UnsignedByteType> cB = inputB.cursor();
		int nb_pixel = 0;
		while (cR.hasNext() && cG.hasNext() && cB.hasNext()) {
			cR.fwd();
			cG.fwd();
			cB.fwd();
			float hsv[] = new float[3];
			rgbToHsv(cR.get().get(), cG.get().get(), cB.get().get(), hsv);
			int val = (int) hsv[2];
			his[val] = his[val] + 1;
			nb_pixel++;

		}
		int his_c[] = new int[256];
		for (int i = 0; i < 0; i++) {
			his_c[i] = 0;
		}

		for (int i = 0; i < 256; i++) {
			for (int j = 0; j <= i; j++) {
				his_c[i] = his_c[i] + his[j];
			}

		}

		final IntervalView<UnsignedByteType> inputR2 = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG2 = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB2 = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR2 = inputR2.cursor();
		final Cursor<UnsignedByteType> cG2 = inputG2.cursor();
		final Cursor<UnsignedByteType> cB2 = inputB2.cursor();
		while (cR2.hasNext() && cG2.hasNext() && cB2.hasNext()) {
			cR2.fwd();
			cG2.fwd();
			cB2.fwd();
			float hsv[] = new float[3];
			rgbToHsv(cR2.get().get(), cG2.get().get(), cB2.get().get(), hsv);
			int val_pixel = (int) hsv[2];
			int new_val = (int) ((his_c[val_pixel] * 255) / nb_pixel);
			if (new_val > 255) {
				new_val = 255;
			}
			if (new_val < 0) {
				new_val = 0;
			}
			hsv[2] = new_val;
			int rgb[] = new int[3];
			hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);

			cR2.get().set(rgb[0]);
			cG2.get().set(rgb[1]);
			cB2.get().set(rgb[2]);

		}
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(input));

		return image_result;

	}

	public Image modifierContrasteHistAvecCanalS(long id) throws FormatException, IOException { // permet de modifier le
																								// contraste de l'image
																								// qui a pour
																								// identifiant le
																								// parametre <<id>> en
																								// utilisant le canal S
																								// avec la methode
																								// d'egalisation
																								// d'histogramme
		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		int his[] = new int[256];
		for (int i = 0; i < 256; i++) {
			his[i] = 0;
		}
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR = inputR.cursor();
		final Cursor<UnsignedByteType> cG = inputG.cursor();
		final Cursor<UnsignedByteType> cB = inputB.cursor();
		int nb_pixel = 0;
		while (cR.hasNext() && cG.hasNext() && cB.hasNext()) {
			cR.fwd();
			cG.fwd();
			cB.fwd();
			float hsv[] = new float[3];
			rgbToHsv(cR.get().get(), cG.get().get(), cB.get().get(), hsv);
			int val = (int) hsv[1];
			his[val] = his[val] + 1;
			nb_pixel++;

		}
		int his_c[] = new int[256];
		for (int i = 0; i < 0; i++) {
			his_c[i] = 0;
		}

		for (int i = 0; i < 256; i++) {
			for (int j = 0; j <= i; j++) {
				his_c[i] = his_c[i] + his[j];
			}

		}

		final IntervalView<UnsignedByteType> inputR2 = Views.hyperSlice(input, 2, 0);
		final IntervalView<UnsignedByteType> inputG2 = Views.hyperSlice(input, 2, 1);
		final IntervalView<UnsignedByteType> inputB2 = Views.hyperSlice(input, 2, 2);
		final Cursor<UnsignedByteType> cR2 = inputR2.cursor();
		final Cursor<UnsignedByteType> cG2 = inputG2.cursor();
		final Cursor<UnsignedByteType> cB2 = inputB2.cursor();
		while (cR2.hasNext() && cG2.hasNext() && cB2.hasNext()) {
			cR2.fwd();
			cG2.fwd();
			cB2.fwd();
			float hsv[] = new float[3];
			rgbToHsv(cR2.get().get(), cG2.get().get(), cB2.get().get(), hsv);
			int val_pixel = (int) hsv[1];
			int new_val = (int) ((his_c[val_pixel] * 255) / nb_pixel);
			if (new_val > 255) {
				new_val = 255;
			}
			if (new_val < 0) {
				new_val = 0;
			}
			hsv[1] = new_val;
			int rgb[] = new int[3];
			hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);

			cR2.get().set(rgb[0]);
			cG2.get().set(rgb[1]);
			cB2.get().set(rgb[2]);

		}
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(input));

		return image_result;

	}

	public static int calcul(int x, int y) { // pour calculer la valeur du masque
		double val = (x * x + y * y);
		double sigma = 4 / 3;
		double k = val / (2 * (sigma * sigma));
		return (int) (Math.exp(-k)); //

	}

	public Image convolution(long id, int[][] kernel) throws FormatException, IOException { // prend en parametre <<id>>
																							// qui correspond a
																							// l'identifiant de l'image
																							// qui serai traité et
																							// <<kernel>> qui correspond
																							// a la matrice de
																							// convolution
		int s = 0;
		int taille = 0;
		for (int[] t : kernel) {
			for (int val : t) {
				taille += val;
			}
		}
		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> output = ImageConverter.imageFromJPEGBytes(image.getData());
		int size = (int) ((Math.sqrt(kernel.length * kernel.length) - 1) / 2);
		final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> extendedView = Views
				.extendZero(input);
		final RandomAccess<UnsignedByteType> rIn = extendedView.randomAccess();
		final RandomAccess<UnsignedByteType> rOut = output.randomAccess();
		final int iw = (int) input.max(0);
		final int ih = (int) input.max(1);
		for (int channel = 0; channel <= 2; channel++) {
			for (int x = 0; x <= iw - 1; ++x) {
				for (int y = 0; y <= ih - 1; ++y) {
					s = 0;
					for (int u = -size; u < size + 1; u++) {
						for (int v = -size; v < size + 1; v++) {

							rIn.setPosition((x + u), 0);
							rIn.setPosition((y + v), 1);
							rIn.setPosition(channel, 2);
							int val_i = (rIn.get().get()) * kernel[u + size][v + size];
							s += val_i;

						}
					}

					rOut.setPosition(x, 0);
					rOut.setPosition(y, 1);
					rOut.setPosition(channel, 2);
					rOut.get().set(s / taille);
				}
			}
		}
		Image image_result = new Image(image.getName(), ImageConverter.imageToJPEGBytes(output));

		return image_result;

	}

	public Image gaussFilterImgLib(long id, int size) throws FormatException, IOException { // applique une filtre
																							// gaussien a l'image qui a
																							// pour identifiant <<id>>
																							// avec comme le parametre
																							// <<size>> comme taille de
																							// la matrice
		int[][] kernel = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				kernel[i][j] = 1 + calcul(i - 2, j - 2);
				System.out.println(kernel[i][j]);
			}
		}

		return convolution(id, kernel);

	}

	public String getSize(long id) throws FormatException, IOException { // prend en parametre un id et return sa taille
																			// au format "" + width + "*" + height + "*"
																			// + color

		Image image = images.get(id);
		// ImageConverter converter = new ImageConverter();
		SCIFIOImgPlus<UnsignedByteType> input = ImageConverter.imageFromJPEGBytes(image.getData());
		final int iw = (int) input.max(0);
		final int ih = (int) input.max(1);
		final Cursor<UnsignedByteType> cursor = input.cursor();
		cursor.fwd();

		long color = input.numDimensions();

		// final int dim = (int) input.max(2);
		// input.getImg().size() / iw * ih;
		return "" + iw + "*" + ih + "*" + color;

	}

	public static String getExtension(String fileName) { // prend en parametre le nom d'un fichier et return son
															// extension
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		return extension;
	}

	public static boolean extensionIsTif(String extension) { // retourne vrai si l'extension en parametre est tif et
																// faux sinon
		if (extension == null || extension.isEmpty())
			return false;
		else
			return extension.equals("tif");

	}

	public static boolean extensionIsJpeg(String extension) { // retourn vrai si l'extension en parametre est jpeg et
																// faux sinon
		if (extension == null || extension.isEmpty())
			return false;
		else
			return extension.equals("jpeg") || extension.equals("jpg");

	}

	public static boolean isValidExtension(String extension) { // prend en parametre une extension et dit si elle est
																// valide ou non;
		if (extension == null || extension.isEmpty())
			return false;
		else
			return extensionIsJpeg(extension) || extensionIsTif(extension); // on accepte uniquement les images au
																			// format jpeg et tif

	}

	public boolean isValidId(long id) {
		boolean isValidId = false; // par defaut on suppose que l'id passée est invalid
		List<Image> array = new ArrayList<Image>(); // on cree une list vide qui va contenir les images present sur le
													// serveur
		array = retrieveAll(); // on recuppere tout les images et on les stockes dans la list
		for (Image img : array) { // on parcour toute la list
			if (img.getId() == id) {
				isValidId = true; // on a trouvé une image qui a le meme id que l'id passée dans l'url alors on
									// valid id
			}
		}
		return isValidId;

	}

	public void loadImagesFromDirectory(File folder) throws IOException { // prend en parametre un dossier et charge
																			// tout les images qui s'y trouve
		if (folder.isDirectory()) {
			FileFilter imageFileFilter = new FileFilter() {
				@Override
				public boolean accept(File file) { // permet de trier les fichiers valides

					String fileName = file.getName();
					// on extrait l'extension du fichier puis on vérifie qu'elle existe dans le
					// dictionnaire
					String extension = getExtension(fileName);
					return isValidExtension(extension) || file.isDirectory();
				}
			};
			if (folder.listFiles(imageFileFilter).length != 0) {
				for (File imageFile : folder.listFiles(imageFileFilter)) {
					if (extensionIsTif(getExtension(imageFile.getName()))) { // on test si l'image est de type tif
						final BufferedImage tif = ImageIO.read(imageFile);
						ImageIO.write(tif, "jpg", imageFile); // on converti les images au format tif en jpeg
					}
					if (imageFile.isFile()) { // si c'est un fichier on le charge directement
						byte[] fileContent = Files.readAllBytes(imageFile.toPath());
						Image image = new Image(imageFile.getName(), fileContent);
						if (image != null) {
							images.put(image.getId(), image);
						}
					}
					if (imageFile.isDirectory()) { // si c'est un repectoir , on charge les images qui s'y trouve
													// reccursivement
						loadImagesFromDirectory(imageFile);
					}

				}
			}
		}
	}

}
