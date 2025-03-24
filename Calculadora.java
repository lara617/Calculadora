import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Calculadora extends JFrame implements ActionListener {

    private JTextField campoTexto;
    private JTextArea historicoArea;
    private JPanel painelBotoes, painelFuncoes;
    private boolean modoEscuro = false;
    private boolean modoAvancado = false;
    private double numero1 = 0, numero2 = 0, resultado = 0, memoria = 0;
    private String operador = "";
    private boolean novoCalculo = false;
    private final DecimalFormat df = new DecimalFormat("#.########");
    private final ArrayList<String> historico = new ArrayList<>();

    public Calculadora() {
        setTitle("Calculadora Completa");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        campoTexto = new JTextField();
        campoTexto.setFont(new Font("Arial", Font.BOLD, 28));
        campoTexto.setHorizontalAlignment(SwingConstants.RIGHT);
        campoTexto.setEditable(false);
        campoTexto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        historicoArea = new JTextArea();
        historicoArea.setEditable(false);
        historicoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollHistorico = new JScrollPane(historicoArea);
        scrollHistorico.setPreferredSize(new Dimension(200, 0));

        add(campoTexto, BorderLayout.NORTH);
        add(scrollHistorico, BorderLayout.EAST);

        painelBotoes = new JPanel(new GridLayout(6, 4, 5, 5));
        painelFuncoes = new JPanel(new GridLayout(2, 3, 5, 5));

        String[] botoes = {
            "C", "←", "%", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "√", "=",
            "M+", "M-", "MR", "MC"
        };

        for (String txt : botoes) {
            adicionarBotao(painelBotoes, txt);
        }

        // Botões modo avançado
        String[] avancados = {"sin", "cos", "tan", "log", "exp", "x²"};
        for (String txt : avancados) {
            adicionarBotao(painelFuncoes, txt);
        }

        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));
        painelCentral.add(painelBotoes, BorderLayout.CENTER);
        painelCentral.add(painelFuncoes, BorderLayout.SOUTH);
        add(painelCentral, BorderLayout.CENTER);

        JPanel painelOpcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botaoTema = new JButton("☀/🌙 Tema");
        botaoTema.addActionListener(e -> alternarTema());
        JButton botaoAvancado = new JButton("Modo Avançado");
        botaoAvancado.addActionListener(e -> alternarModoAvancado());

        painelOpcoes.add(botaoTema);
        painelOpcoes.add(botaoAvancado);
        add(painelOpcoes, BorderLayout.SOUTH);

        alternarTema(); // aplicar tema padrão
        alternarModoAvancado(); // ocultar funções avançadas por padrão
        setVisible(true);
    }

    private void adicionarBotao(JPanel painel, String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 20));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        botao.setBackground(Color.decode("#e0e0e0"));
        botao.setForeground(Color.BLACK);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setActionCommand(texto);
        botao.addActionListener(this);
        botao.setPreferredSize(new Dimension(60, 60));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        botao.setOpaque(true);
        painel.add(botao);
    }

    private void alternarTema() {
        modoEscuro = !modoEscuro;
        Color fundo = modoEscuro ? Color.decode("#1e1e1e") : Color.WHITE;
        Color texto = modoEscuro ? Color.WHITE : Color.BLACK;

        getContentPane().setBackground(fundo);
        campoTexto.setBackground(fundo);
        campoTexto.setForeground(texto);
        historicoArea.setBackground(fundo);
        historicoArea.setForeground(texto);

        atualizarCores(painelBotoes, fundo, texto);
        atualizarCores(painelFuncoes, fundo, texto);
    }

    private void atualizarCores(JPanel painel, Color fundo, Color texto) {
        for (Component comp : painel.getComponents()) {
            if (comp instanceof JButton btn) {
                btn.setBackground(modoEscuro ? Color.decode("#333") : Color.decode("#e0e0e0"));
                btn.setForeground(texto);
            }
        }
    }

    private void alternarModoAvancado() {
        modoAvancado = !modoAvancado;
        painelFuncoes.setVisible(modoAvancado);
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        try {
            if (cmd.matches("[0-9]") || cmd.equals(".")) {
                if (novoCalculo) {
                    campoTexto.setText("");
                    novoCalculo = false;
                }
                campoTexto.setText(campoTexto.getText() + cmd);
            } else if (cmd.matches("[\\+\\-\\*/%]")) {
                numero1 = Double.parseDouble(campoTexto.getText());
                operador = cmd;
                campoTexto.setText("");
            } else if (cmd.equals("=")) {
                numero2 = Double.parseDouble(campoTexto.getText());
                switch (operador) {
                    case "+": resultado = numero1 + numero2; break;
                    case "-": resultado = numero1 - numero2; break;
                    case "*": resultado = numero1 * numero2; break;
                    case "/": resultado = numero2 != 0 ? numero1 / numero2 : Double.NaN; break;
                    case "%": resultado = numero1 * (numero2 / 100); break;
                }
                campoTexto.setText(df.format(resultado));
                historico.add(numero1 + " " + operador + " " + numero2 + " = " + df.format(resultado));
                atualizarHistorico();
                novoCalculo = true;
            } else if (cmd.equals("√")) {
                double valor = Double.parseDouble(campoTexto.getText());
                campoTexto.setText(df.format(Math.sqrt(valor)));
                novoCalculo = true;
            } else if (cmd.equals("x²")) {
                double valor = Double.parseDouble(campoTexto.getText());
                campoTexto.setText(df.format(Math.pow(valor, 2)));
                novoCalculo = true;
            } else if (cmd.equals("sin")) {
                double valor = Double.parseDouble(campoTexto.getText());
                campoTexto.setText(df.format(Math.sin(Math.toRadians(valor))));
                novoCalculo = true;
            } else if (cmd.equals("cos")) {
                double valor = Double.parseDouble(campoTexto.getText());
                campoTexto.setText(df.format(Math.cos(Math.toRadians(valor))));
                novoCalculo = true;
            } else if (cmd.equals("tan")) {
                double valor = Double.parseDouble(campoTexto.getText());
                campoTexto.setText(df.format(Math.tan(Math.toRadians(valor))));
                novoCalculo = true;
            } else if (cmd.equals("log")) {
                double valor = Double.parseDouble(campoTexto.getText());
                campoTexto.setText(df.format(Math.log10(valor)));
                novoCalculo = true;
            } else if (cmd.equals("exp")) {
                double valor = Double.parseDouble(campoTexto.getText());
                campoTexto.setText(df.format(Math.exp(valor)));
                novoCalculo = true;
            } else if (cmd.equals("C")) {
                campoTexto.setText("");
                operador = "";
            } else if (cmd.equals("←")) {
                String texto = campoTexto.getText();
                if (!texto.isEmpty()) {
                    campoTexto.setText(texto.substring(0, texto.length() - 1));
                }
            } else if (cmd.equals("M+")) {
                memoria += Double.parseDouble(campoTexto.getText());
            } else if (cmd.equals("M-")) {
                memoria -= Double.parseDouble(campoTexto.getText());
            } else if (cmd.equals("MR")) {
                campoTexto.setText(df.format(memoria));
            } else if (cmd.equals("MC")) {
                memoria = 0;
            }

        } catch (Exception ex) {
            campoTexto.setText("Erro");
        }
    }

    private void atualizarHistorico() {
        StringBuilder sb = new StringBuilder();
        for (String s : historico) sb.append(s).append("\n");
        historicoArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculadora::new);
    }
}
