import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Rectangle2D;

public class FractalExplorer {
    private int displaySize;
    private JImageDisplay imageDisplay;
    private FractalGenerator fractalGenerator;
    private Rectangle2D.Double range;
    private JComboBox comboBox;
    private int rowsRemaining;
    private JButton buttonReset;
    private JButton buttonSave;

    private FractalExplorer (int displaySize) {
        this.displaySize = displaySize;
        this.fractalGenerator = new Mandelbrot();
        this.range = new Rectangle2D.Double(0,0,0,0);
        fractalGenerator.getInitialRange(this.range);
    }

    // точка входа
    public static void main(String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(600);
        fractalExplorer.setGUI();
        fractalExplorer.drawFractal();
    }

    // задание интерфейса
    public void setGUI() {
        JFrame frame = new JFrame("Fractal Generator");
        JPanel jPanel_1 = new JPanel();
        JPanel jPanel_2 = new JPanel();
        JLabel label = new JLabel("Fractal:");

        imageDisplay = new JImageDisplay(displaySize, displaySize);
        imageDisplay.addMouseListener(new MouseListener());

        // выпадающий список
        comboBox = new JComboBox();
        comboBox.addItem(new Mandelbrot());
        comboBox.addItem(new Tricorn());
        comboBox.addItem(new BurningShip());
        comboBox.addActionListener(new ActionHandler());

        // кнопка reset
        buttonReset = new JButton("Reset");
        buttonReset.setActionCommand("Reset");
        buttonReset.addActionListener(new ActionHandler());

        // кнопка сохранить
        buttonSave = new JButton("Save image");
        buttonSave.setActionCommand("Save");
        buttonSave.addActionListener(new ActionHandler());

        jPanel_1.add(label, BorderLayout.CENTER);
        jPanel_1.add(comboBox, BorderLayout.CENTER);
        jPanel_2.add(buttonReset, BorderLayout.CENTER);
        jPanel_2.add(buttonSave, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(imageDisplay, BorderLayout.CENTER);
        frame.add(jPanel_1, BorderLayout.NORTH);
        frame.add(jPanel_2, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    // отрисовка фрактала в JImageDisplay
    private void drawFractal() {
        // отключаем интерфейс на момент рисования
        enableGUI(false);
        rowsRemaining = displaySize;
        for (int i = 0; i < displaySize; i++) {
            FractalWorker drawRow = new FractalWorker(i);
            drawRow.execute();
        }
    }

    // включение - отключение gui
    public void enableGUI(boolean b) {
        buttonSave.setEnabled(b);
        buttonReset.setEnabled(b);
        comboBox.setEnabled(b);
    }

    // обработчик кнопок
    public class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Reset")) {
                // перерисовка фрактала
                fractalGenerator.getInitialRange(range);
                drawFractal();
            } else if (e.getActionCommand().equals("Save")) {
                // сохранение
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int t = fileChooser.showSaveDialog(imageDisplay);
                if (t == JFileChooser.APPROVE_OPTION) {
                    try {
                        ImageIO.write(imageDisplay.getImage(), "png", fileChooser.getSelectedFile());
                    } catch (NullPointerException | IOException ee) {
                        JOptionPane.showMessageDialog(imageDisplay, ee.getMessage(), "Cannot save image", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                fractalGenerator = (FractalGenerator) comboBox.getSelectedItem();
                range = new Rectangle2D.Double(0,0,0,0);
                fractalGenerator.getInitialRange(range);
                drawFractal();
            }
        }
    }

    public class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            double x = FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, e.getX());
            double y = FractalGenerator.getCoord(range.y, range.y + range.width, displaySize, e.getY());
            fractalGenerator.recenterAndZoomRange(range, x, y, 0.5);
            drawFractal();
        }
    }

    //Вычисляет значение цвета для 1 строки фрактала
    public class FractalWorker extends SwingWorker<Object, Object> {
        //obj1 - тип значения,
        // возвращаемого функцией doInBackground(), когда задача полностью выполнена
        // Тип obj2 используется, когда фоновая задача возвращает промежуточные значения во время выполнения
        // y координата вычисляемой строки
        private int y_coord;
        //Массив чисел для хранения вычисленных значений RGB для каждого пикселя в этой строке
        private int[] rgb;

        public FractalWorker(int y_coord) {
            this.y_coord = y_coord;
        }

        //Вызывается в фоновом потоке и отвечает за выполнение длительной задачи.
        @Override
        protected Object doInBackground() throws Exception {
            //Для сохранения каждого значения RGB в соответствующем элементе целочисленного массива
            rgb = new int[displaySize];
            // Проходимся по всем пикселям в строке
            for (int i = 0; i < displaySize; i++) {
                // Вычисляем кол-во итераций для координат
                int count = fractalGenerator.numIterations(FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, i),
                        // Находим соответствующие координаты x и y
                        FractalGenerator.getCoord(range.y, range.y+range.width, displaySize, y_coord));
                if (count == -1)
                    rgb[i] = 0;
                else {
                    // Выбираем значение цвета на основе числа итераций
                    double hue = 0.7f + (float) count / 200f;
                    int rgbColor = Color.HSBtoRGB((float) hue, 1f, 1f);
                    // Обновляем массив с цветами текущего пикселя
                    rgb[i] = rgbColor;
                }
            }
            return null;
        }

        //Вызывается, когда фоновая задача завершена, и этот метод вызывается из потока обработки событий Swing
        @Override
        protected void done() {
            //Перебираем массив строк данных, рисуя пиксели, которые были вычислены в doInBackground ()
            for (int i = 0; i < displaySize; i++) {
                imageDisplay.drawPixel(i, y_coord, rgb[i]);
            }
            imageDisplay.repaint(0,0,y_coord,displaySize,1);
            rowsRemaining--;
            if (rowsRemaining == 0)
                enableGUI(true);
        }
    }
}