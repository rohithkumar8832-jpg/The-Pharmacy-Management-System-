// =====================================================================
//  MediStock — Pharmacy Management System (single-file Java/Swing build)
//  New in this version:
//   • Theme engine: Classic, Glassy Glow, Matrix, Cyberpunk, Retro Future,
//     Blinding Lights, Ocean Blue, Ruby Red, Emerald, Midnight, Solar Amber, Violet Dream
//   • Expiry reminders (badge, bell, startup toast, popup on save)
//   • Dashboard timer widget (live clock + focus countdown + session data)
//  Run:  javac PharmacyApp.java && java PharmacyApp
//  Or :  java PharmacyApp.java          (JDK 11+ single-file launch)
// =====================================================================

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class PharmacyApp {

    /* ================================================================
     *  MAIN
     * ================================================================ */
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new PharmacyApp().start());
    }

    /* ================================================================
     *  THEME ENGINE
     * ================================================================ */
    static class Theme {
        final String name;
        final boolean dark, glow;
        final Color ink, muted, line, paper, paper2, card, card2, nav, navHover, navMuted,
                primary, primaryDark, green, red, amber, rowSel, glowColor, topbar;

        Theme(String name, boolean dark, boolean glow,
              int ink, int muted, int line, int paper, int paper2, int card, int card2,
              int nav, int navHover, int navMuted,
              int primary, int primaryDark, int green, int red, int amber,
              int rowSel, int glowColor, int topbar) {
            this.name = name; this.dark = dark; this.glow = glow;
            this.ink = new Color(ink);
            this.muted = new Color(muted);
            this.line = new Color(line);
            this.paper = new Color(paper);
            this.paper2 = new Color(paper2);
            this.card = new Color(card);
            this.card2 = new Color(card2);
            this.nav = new Color(nav);
            this.navHover = new Color(navHover);
            this.navMuted = new Color(navMuted);
            this.primary = new Color(primary);
            this.primaryDark = new Color(primaryDark);
            this.green = new Color(green);
            this.red = new Color(red);
            this.amber = new Color(amber);
            this.rowSel = new Color(rowSel);
            this.glowColor = new Color(glowColor);
            this.topbar = new Color(topbar);
        }
    }

    static final List<Theme> THEMES = buildThemes();

    static Theme THEME;

    static Color INK, MUTED, LINE, PAPER, PAPER2, CARD, CARD2, NAV, NAV_HOVER, NAV_MUTED,
            BLUE, BLUE_DARK, GREEN, RED, AMBER, ROW_SEL, GLOW, TOPBAR;

    static { applyTheme(THEMES.get(0)); }

    static void applyTheme(Theme t) {
        THEME = t;
        INK = t.ink; MUTED = t.muted; LINE = t.line; PAPER = t.paper; PAPER2 = t.paper2;
        CARD = t.card; CARD2 = t.card2; NAV = t.nav; NAV_HOVER = t.navHover; NAV_MUTED = t.navMuted;
        BLUE = t.primary; BLUE_DARK = t.primaryDark; GREEN = t.green; RED = t.red; AMBER = t.amber;
        ROW_SEL = t.rowSel; GLOW = t.glowColor; TOPBAR = t.topbar;
    }

    static List<Theme> buildThemes() {
        List<Theme> l = new ArrayList<>();
        // name, dark, glow, ink, muted, line, paper, paper2, card, card2,
        // nav, navHover, navMuted, primary, primaryDark, green, red, amber, rowSel, glow, topbar
        l.add(new Theme("Classic", false, false,
                0x18202e, 0x687386, 0xe6eaf0, 0xf5f7fb, 0xeef2fa, 0xffffff, 0xfbfcff,
                0x17243a, 0x273953, 0xaab6c9,
                0x4568e8, 0x3150c4, 0x14836d, 0xb63743, 0x9a6406,
                0xf2f6ff, 0x6381ff, 0xffffff));
        l.add(new Theme("Glassy Glow", false, true,
                0x141a33, 0x5f6b8f, 0xd7e0f5, 0xeef3ff, 0xdde7ff, 0xffffff, 0xf2f6ff,
                0x1f2a55, 0x2e3d75, 0xa8b5e0,
                0x6a5cff, 0x5044d6, 0x0f9d7a, 0xd63a55, 0xb0761a,
                0xeaf0ff, 0x8f7cff, 0xffffff));
        l.add(new Theme("Matrix", true, true,
                0x9dffb0, 0x4f9a67, 0x14432a, 0x030b06, 0x06170d, 0x07160c, 0x0a2414,
                0x02100a, 0x0c2a18, 0x4c9a68,
                0x1fe06a, 0x14b455, 0x2eff8a, 0xff5d6c, 0xffd166,
                0x0d2c19, 0x2bff88, 0x04120b));
        l.add(new Theme("Cyberpunk", true, true,
                0xf2e9ff, 0xa07ec9, 0x3a1d63, 0x0b0616, 0x0f0722, 0x150b2b, 0x1d0f39,
                0x120526, 0x2a0f4d, 0x9d7ac9,
                0xff2bd1, 0xc81fa6, 0x00e5c0, 0xff4d6d, 0xffc247,
                0x24103f, 0xff2bd1, 0x0e0520));
        l.add(new Theme("Retro Future", true, true,
                0xffe9c7, 0x8fb3bd, 0x1d4a58, 0x0a1a22, 0x0c222b, 0x0f2731, 0x12323d,
                0x07141b, 0x123340, 0x7fa6b2,
                0xffa62b, 0xd9851a, 0x2ee6c5, 0xff6b57, 0xffc857,
                0x123640, 0xffb347, 0x081820));
        l.add(new Theme("Blinding Lights", false, true,
                0x0b1020, 0x5d6b85, 0xdfe7f5, 0xffffff, 0xeff4ff, 0xffffff, 0xf7faff,
                0x0b1020, 0x1c2740, 0xa9b6cf,
                0x1f6bff, 0x0f4fd8, 0x00b894, 0xff2d55, 0xffb020,
                0xeaf2ff, 0x4d8bff, 0xffffff));
        l.add(new Theme("Ocean Blue", false, true,
                0x0d2436, 0x5b7a91, 0xd6e6f2, 0xeef5fb, 0xdfeefb, 0xffffff, 0xf4faff,
                0x0b2d45, 0x14425f, 0x9dbdd2,
                0x1a7fd4, 0x1466ad, 0x128a6f, 0xc23a4a, 0xb0761a,
                0xe9f4fd, 0x3fa9ff, 0xffffff));
        l.add(new Theme("Ruby Red", false, true,
                0x2a1116, 0x8a6470, 0xf0d9dc, 0xfdf3f3, 0xf9e6e6, 0xffffff, 0xfff8f8,
                0x2b0d14, 0x45151f, 0xc9a3ab,
                0xd7263d, 0xae1c30, 0x1a8a63, 0xb3202f, 0xb0751a,
                0xfdeef0, 0xff4d6d, 0xffffff));
        l.add(new Theme("Emerald", false, true,
                0x10241b, 0x5f8371, 0xd3ecdf, 0xf0faf5, 0xe1f5ea, 0xffffff, 0xf7fdfa,
                0x0d2a1e, 0x174030, 0x9dc4ae,
                0x128a5f, 0x0c6a49, 0x0f9d63, 0xc0392b, 0xa9761a,
                0xe9f8f0, 0x2fd68a, 0xffffff));
        l.add(new Theme("Midnight Dark", true, false,
                0xe6ecf7, 0x93a1b8, 0x263149, 0x0e1420, 0x0a0f19, 0x161e2e, 0x1b2537,
                0x0a0f19, 0x1d2739, 0x93a1b8,
                0x5b8cff, 0x4370e0, 0x2ecf9f, 0xff6b7a, 0xffc247,
                0x1e2a3f, 0x5b8cff, 0x101725));
        l.add(new Theme("Solar Amber", true, true,
                0xffeeda, 0xc0a77f, 0x4a3517, 0x140f08, 0x1a1309, 0x241a0e, 0x2b1f10,
                0x1a1207, 0x342511, 0xc0a77f,
                0xffa62b, 0xd9851a, 0x63d29b, 0xff6b57, 0xffc857,
                0x3a2a12, 0xffa62b, 0x1a1207));
        l.add(new Theme("Violet Dream", true, true,
                0xf0e9ff, 0xa99bd0, 0x3b2b66, 0x0d0a1a, 0x120d24, 0x171129, 0x1e1636,
                0x0b0817, 0x241a44, 0xa99bd0,
                0xa06bff, 0x7e4fe0, 0x39d98a, 0xff5d8f, 0xffc247,
                0x261b45, 0xa06bff, 0x0f0b1e));
        return l;
    }

    /* ================================================================
     *  TYPOGRAPHY
     * ================================================================ */
    static final String UI_FONT = pickFont();
    static String pickFont() {
        Set<String> have = new HashSet<>();
        for (String n : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
            have.add(n);
        for (String p : new String[]{"Segoe UI", "Inter", "Helvetica Neue", "Arial", "DejaVu Sans"})
            if (have.contains(p)) return p;
        return "Dialog";
    }
    static Font f(int size, int style) { return new Font(UI_FONT, style, size); }

    static final LocalDate TODAY = LocalDate.of(2026, 10, 23);   // demo date (same as the HTML)

    /* ================================================================
     *  MODEL CLASSES
     * ================================================================ */
    static class Row {
        final long id;
        final LinkedHashMap<String, Object> v = new LinkedHashMap<>();
        Row(long id) { this.id = id; }
        Row set(String k, Object val) { v.put(k, val); return this; }
        Object get(String k) { return v.get(k); }
        String s(String k) { Object o = v.get(k); return o == null ? "" : String.valueOf(o); }
        double d(String k) {
            Object o = v.get(k);
            if (o instanceof Number) return ((Number) o).doubleValue();
            try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0; }
        }
    }

    static Row row(long id, Object... kv) {
        Row r = new Row(id);
        for (int i = 0; i + 1 < kv.length; i += 2) r.set(String.valueOf(kv[i]), kv[i + 1]);
        return r;
    }

    static class Field {
        final String name, label, type;
        final String[] options;
        Field(String n, String l, String t, String... o) { name = n; label = l; type = t; options = o; }
    }

    static class Module {
        final String key, title, subtitle, button;
        final List<Field> fields = new ArrayList<>();
        final List<String[]> cols = new ArrayList<>();
        Module(String k, String t, String s, String b) { key = k; title = t; subtitle = s; button = b; }
        Module f(String n, String l, String t, String... o) { fields.add(new Field(n, l, t, o)); return this; }
        Module c(String n, String l) { cols.add(new String[]{n, l}); return this; }
    }

    static class CalEvent {
        long id;
        String title;
        LocalDate date;
        LocalTime start, end;
        boolean allDay;
        String cal = "General";
        String location = "", notes = "";
    }

    static class ExpiryAlert {
        String medicine = "", batch = "", expiry = "";
        long days;
        boolean expired;
        boolean batchLevel;
    }

    /* ================================================================
     *  UI BUILDING BLOCKS
     * ================================================================ */
    static class RoundBorder extends AbstractBorder {
        final int radius; final Color fill, stroke;
        RoundBorder(int radius, Color fill, Color stroke) {
            this.radius = radius; this.fill = fill; this.stroke = stroke;
        }
        public Insets getBorderInsets(Component c) { return new Insets(0, 0, 0, 0); }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (fill != null) { g2.setColor(fill); g2.fillRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2); }
            if (stroke != null) { g2.setColor(stroke); g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2); }
            g2.dispose();
        }
    }

    static class RoundButton extends JButton {
        Color bg, hoverBg, stroke;
        int radius;
        RoundButton(String text, Color bg, Color hoverBg, Color fg, Color stroke, int radius) {
            super(text);
            this.bg = bg; this.hoverBg = hoverBg; this.stroke = stroke; this.radius = radius;
            setForeground(fg);
            setFont(f(12, Font.BOLD));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 13, 9, 13));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = (getModel().isRollover() && hoverBg != null) ? hoverBg : bg;
            if (c != null) { g2.setColor(c); g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius * 2, radius * 2); }
            if (stroke != null) { g2.setColor(stroke); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius * 2, radius * 2); }
            g2.dispose();
            super.paintComponent(g);
        }
        void setActiveState(boolean on) {
            bg = on ? NAV_HOVER : null;
            setForeground(on ? Color.WHITE : NAV_MUTED);
            repaint();
        }
    }

    /** Vertical layout that stretches every child to full width. */
    static class VStackLayout implements LayoutManager {
        private final int gap;
        VStackLayout(int gap) { this.gap = gap; }
        public void addLayoutComponent(String name, Component comp) { }
        public void removeLayoutComponent(Component comp) { }
        public Dimension preferredLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            int w = 0, h = 0, n = 0;
            for (Component c : parent.getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = c.getPreferredSize();
                w = Math.max(w, d.width); h += d.height; n++;
            }
            if (n > 1) h += gap * (n - 1);
            return new Dimension(w + in.left + in.right, h + in.top + in.bottom);
        }
        public Dimension minimumLayoutSize(Container parent) { return preferredLayoutSize(parent); }
        public void layoutContainer(Container parent) {
            Insets in = parent.getInsets();
            int width = Math.max(0, parent.getWidth() - in.left - in.right);
            int y = in.top;
            for (Component c : parent.getComponents()) {
                if (!c.isVisible()) continue;
                int h = c.getPreferredSize().height;
                c.setBounds(in.left, y, width, h);
                y += h + gap;
            }
        }
    }

    /** Page background with a soft gradient (glassy feel). */
    static class PagePanel extends JPanel implements Scrollable {
        PagePanel() { setLayout(new VStackLayout(18)); setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, PAPER, getWidth(), getHeight(), PAPER2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 120; }
        public boolean getScrollableTracksViewportWidth() { return true; }
        public boolean getScrollableTracksViewportHeight() {
            return getParent() != null && getPreferredSize().height < getParent().getHeight();
        }
    }

    /** Glass card: gradient fill + optional neon glow ring. */
    static class GlassPanel extends JPanel {
        int radius = 15;
        Color accent;
        GlassPanel(LayoutManager lm) { super(lm); setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            if (THEME.glow) {
                Color gc = accent != null ? accent : GLOW;
                for (int i = 6; i >= 1; i--) {
                    g2.setColor(new Color(gc.getRed(), gc.getGreen(), gc.getBlue(), Math.max(3, 14 - i * 2)));
                    g2.setStroke(new BasicStroke(i * 2f));
                    g2.drawRoundRect(i, i, Math.max(1, w - 1 - i * 2), Math.max(1, h - 1 - i * 2),
                            radius * 2, radius * 2);
                }
            }

            g2.setPaint(new GradientPaint(0, 0, CARD, w, h, CARD2));
            g2.fillRoundRect(1, 1, Math.max(1, w - 2), Math.max(1, h - 2), radius * 2, radius * 2);

            if (THEME.dark) {
                g2.setColor(new Color(255, 255, 255, 14));
                g2.drawRoundRect(2, 2, Math.max(1, w - 5), Math.max(1, h - 5), radius * 2, radius * 2);
            }

            g2.setColor(THEME.glow ? mix(accent != null ? accent : GLOW, CARD, 0.45) : LINE);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(1, 1, Math.max(1, w - 3), Math.max(1, h - 3), radius * 2, radius * 2);
            g2.dispose();
        }
    }

    static class Pill extends JLabel {
        Color bg = new Color(0xf0f2f6);
        Pill() { setOpaque(false); setBorder(new EmptyBorder(4, 9, 4, 9)); setFont(f(10, Font.BOLD)); }
        void setPill(Color bg, Color fg) { this.bg = bg; setForeground(fg); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Simple rounded progress bar used by the dashboard timer. */
    static class Bar extends JComponent {
        double pct = 1;
        Bar() { setOpaque(false); setPreferredSize(new Dimension(100, 8)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(mix(BLUE, CARD, THEME.dark ? 0.78 : 0.86));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            int w = (int) Math.round(getWidth() * Math.max(0, Math.min(1, pct)));
            if (w > 1) {
                g2.setPaint(new GradientPaint(0, 0, BLUE, w, 0, THEME.dark ? GLOW : BLUE_DARK));
                g2.fillRoundRect(0, 0, w, getHeight(), 8, 8);
            }
            g2.dispose();
        }
    }

    /* ================================================================
     *  APP STATE
     * ================================================================ */
    final Map<String, List<Row>> data = new LinkedHashMap<>();
    final Map<String, Module> modules = new LinkedHashMap<>();
    final List<CalEvent> events = new ArrayList<>();
    final Map<String, RoundButton> navButtons = new LinkedHashMap<>();

    JFrame frame;
    PagePanel page;
    JPanel headActions, viewHolder;
    JLabel crumb, pageTitle, pageSubtitle;
    RoundButton bellBtn, themeBtn;
    String current = "dashboard";

    LocalDate calMonth = TODAY.withDayOfMonth(1);
    LocalDate calSelected = TODAY;

    static final String[][] NAV_ITEMS = {
            {"dashboard",     "▦",  "Dashboard"},
            {"medicines",     "✚",  "Medicines"},
            {"inventory",     "▤",  "Batch inventory"},
            {"suppliers",     "◈",  "Suppliers"},
            {"purchases",     "⇧",  "Purchases"},
            {"sales",         "▣",  "Sales & billing"},
            {"customers",     "◉",  "Customers"},
            {"prescriptions", "Rx", "Prescriptions"},
            {"calendar",      "▩",  "Calendar"},
            {"reports",       "▥",  "Reports & alerts"},
            {"users",         "⚙",  "User accounts"}
    };

    static final String[][] CALENDARS = {
            {"General",     "#4568e8"},
            {"Deliveries",  "#14836d"},
            {"Health camp", "#b63743"},
            {"Reminders",   "#9a6406"}
    };

    /* ================================================================
     *  BOOT
     * ================================================================ */
    void start() {
        defineModules();
        seedData();
        seedEvents();
        buildUi();
        render();

        // first-run expiry reminder (one-shot)
        javax.swing.Timer first = new javax.swing.Timer(1200, e -> {
            int n = computeExpiryAlerts().size();
            if (n > 0) toast("Expiry reminder: " + n + " item(s) need attention — open the bell icon.");
        });
        first.setRepeats(false);
        first.start();

        // keep the bell badge fresh without rebuilding the page
        javax.swing.Timer watch = new javax.swing.Timer(30000, e -> updateBell());
        watch.start();
    }

    /* ---------- module definitions ---------- */
    void defineModules() {
        Module med = new Module("medicines", "Medicines",
                "Catalogue records and medicine-level reorder thresholds.", "Add medicine");
        med.f("name", "Medicine name", "text").f("generic", "Generic name", "text")
           .f("sku", "SKU / product code", "text")
           .f("category", "Category", "select", "Analgesic", "Antibiotic", "Antihistamine", "Gastrointestinal", "Vitamins", "Other")
           .f("strength", "Strength", "text").f("stock", "Current stock", "number")
           .f("reorder", "Reorder threshold", "number").f("price", "Unit selling price", "number")
           .f("expiry", "Nearest expiry (YYYY-MM-DD)", "date")
           .f("prescription", "Prescription required", "select", "No", "Yes");
        med.c("name", "Medicine").c("sku", "SKU").c("category", "Category").c("strength", "Strength")
           .c("stock", "Available stock").c("reorder", "Reorder at").c("price", "Selling price")
           .c("expiry", "Expiry").c("prescription", "Prescription");
        modules.put(med.key, med);

        Module inv = new Module("inventory", "Batch inventory",
                "Stock is tracked per batch, with expiry and selling price.", "Add batch");
        inv.f("medicine", "Medicine", "text").f("batch", "Batch number", "text")
           .f("expiry", "Expiry date", "date").f("qty", "Available quantity", "number")
           .f("supplier", "Supplier", "text").f("cost", "Purchase cost", "number")
           .f("price", "Selling price", "number");
        inv.c("medicine", "Medicine").c("batch", "Batch").c("expiry", "Expiry date")
           .c("qty", "Available qty").c("supplier", "Supplier").c("cost", "Unit cost").c("price", "Selling price");
        modules.put(inv.key, inv);

        Module sup = new Module("suppliers", "Suppliers",
                "Supplier directory for order creation and inventory receiving.", "Add supplier");
        sup.f("name", "Supplier name", "text").f("contact", "Contact person", "text")
           .f("phone", "Phone", "tel").f("email", "Email", "email")
           .f("license", "License / registration", "text")
           .f("active", "Status", "select", "Active", "Archived");
        sup.c("name", "Supplier").c("contact", "Contact").c("phone", "Phone")
           .c("email", "Email").c("license", "License").c("active", "Status");
        modules.put(sup.key, sup);

        Module pur = new Module("purchases", "Purchases",
                "Purchase orders are drafts until received; receipts add batches to stock.", "New purchase");
        pur.f("number", "Purchase reference", "text").f("supplier", "Supplier", "text")
           .f("date", "Purchase date", "date").f("items", "Line count", "number")
           .f("total", "Grand total", "number")
           .f("status", "Status", "select", "Draft", "Received", "Cancelled")
           .f("createdBy", "Created by", "text");
        pur.c("number", "Purchase").c("supplier", "Supplier").c("date", "Date").c("items", "Lines")
           .c("total", "Grand total").c("status", "Status").c("createdBy", "Created by");
        modules.put(pur.key, pur);

        Module sal = new Module("sales", "Sales & billing",
                "Invoices preserve historical prices and have an explicit status.", "New sale");
        sal.f("invoice", "Invoice number", "text").f("date", "Sold at", "text")
           .f("customer", "Customer / walk-in", "text").f("items", "Line count", "number")
           .f("subtotal", "Subtotal", "number").f("discount", "Discount", "number")
           .f("tax", "Tax", "number").f("total", "Grand total", "number")
           .f("status", "Status", "select", "Confirmed", "Cancelled")
           .f("createdBy", "Created by", "text");
        sal.c("invoice", "Invoice").c("date", "Date").c("customer", "Customer").c("items", "Lines")
           .c("subtotal", "Subtotal").c("discount", "Discount").c("tax", "Tax").c("total", "Grand total")
           .c("status", "Status").c("createdBy", "Processed by");
        modules.put(sal.key, sal);

        Module cus = new Module("customers", "Customers",
                "Store only contact details needed for the pharmacy workflow.", "Add customer");
        cus.f("name", "Full name", "text").f("phone", "Phone", "tel").f("email", "Email", "email")
           .f("address", "Address", "text").f("active", "Status", "select", "Active", "Archived");
        cus.c("name", "Customer").c("phone", "Phone").c("email", "Email")
           .c("address", "Address").c("active", "Status");
        modules.put(cus.key, cus);

        Module pre = new Module("prescriptions", "Prescriptions",
                "New records are pending until reviewed by a qualified pharmacist.", "Record prescription");
        pre.f("reference", "Reference number", "text").f("customer", "Customer", "text")
           .f("prescriber", "Prescriber name", "text").f("issue", "Issue date", "date")
           .f("valid", "Valid until", "date")
           .f("status", "Review status", "select", "Pending", "Verified", "Rejected")
           .f("reviewer", "Reviewed by", "text");
        pre.c("reference", "Reference").c("customer", "Customer").c("prescriber", "Prescriber")
           .c("issue", "Issue date").c("valid", "Valid until").c("status", "Status").c("reviewer", "Reviewer");
        modules.put(pre.key, pre);

        Module usr = new Module("users", "User accounts",
                "Preview roles: administrator, pharmacist and inventory staff.", "Add user");
        usr.f("username", "Username", "text")
           .f("role", "Role", "select", "ADMIN", "PHARMACIST", "INVENTORY")
           .f("status", "Status", "select", "Active", "Disabled");
        usr.c("username", "Username").c("role", "Role").c("status", "Status");
        modules.put(usr.key, usr);
    }

    /* ---------- sample data ---------- */
    void seedData() {
        data.clear();

        data.put("medicines", new ArrayList<>(List.of(
                row(1, "name", "Paracetamol", "generic", "Paracetamol", "sku", "MED-001", "category", "Analgesic",
                        "strength", "500 mg", "stock", 240, "reorder", 40, "price", 2.5,
                        "expiry", "2027-04-30", "prescription", "No"),
                row(2, "name", "Amoxicillin", "generic", "Amoxicillin", "sku", "MED-002", "category", "Antibiotic",
                        "strength", "250 mg", "stock", 18, "reorder", 30, "price", 8.2,
                        "expiry", "2027-02-28", "prescription", "Yes"),
                row(3, "name", "Cetirizine", "generic", "Cetirizine", "sku", "MED-003", "category", "Antihistamine",
                        "strength", "10 mg", "stock", 72, "reorder", 25, "price", 3.4,
                        "expiry", "2026-11-15", "prescription", "No"),
                row(4, "name", "Omeprazole", "generic", "Omeprazole", "sku", "MED-004", "category", "Gastrointestinal",
                        "strength", "20 mg", "stock", 12, "reorder", 20, "price", 5.9,
                        "expiry", "2026-10-31", "prescription", "No"))));

        data.put("inventory", new ArrayList<>(List.of(
                row(1, "medicine", "Paracetamol", "batch", "PCM-2401", "expiry", "2027-04-30", "qty", 160,
                        "supplier", "NorthStar Pharma", "cost", 1.25, "price", 2.5),
                row(2, "medicine", "Paracetamol", "batch", "PCM-2402", "expiry", "2027-09-30", "qty", 80,
                        "supplier", "NorthStar Pharma", "cost", 1.3, "price", 2.5),
                row(3, "medicine", "Amoxicillin", "batch", "AMX-1108", "expiry", "2027-02-28", "qty", 18,
                        "supplier", "CureWell Distributors", "cost", 4.2, "price", 8.2),
                row(4, "medicine", "Omeprazole", "batch", "OMP-8091", "expiry", "2026-10-31", "qty", 12,
                        "supplier", "CureWell Distributors", "cost", 2.6, "price", 5.9),
                row(5, "medicine", "Cetirizine", "batch", "CTZ-3312", "expiry", "2026-11-05", "qty", 24,
                        "supplier", "NorthStar Pharma", "cost", 1.7, "price", 3.4))));

        data.put("suppliers", new ArrayList<>(List.of(
                row(1, "name", "NorthStar Pharma", "contact", "R. Kumar", "phone", "+91 98765 43210",
                        "email", "orders@northstar.example", "license", "SUP-TN-1024", "active", "Active"),
                row(2, "name", "CureWell Distributors", "contact", "Priya S.", "phone", "+91 98400 22222",
                        "email", "supply@curewell.example", "license", "SUP-TN-2048", "active", "Active"),
                row(3, "name", "MedLine Wholesale", "contact", "Arun M.", "phone", "+91 90000 11000",
                        "email", "sales@medline.example", "license", "SUP-TN-3004", "active", "Active"))));

        data.put("purchases", new ArrayList<>(List.of(
                row(1, "number", "PO-20261022-A12", "supplier", "NorthStar Pharma", "date", "2026-10-22",
                        "items", 3, "total", 18500.0, "status", "Received", "createdBy", "admin"),
                row(2, "number", "PO-20261023-B24", "supplier", "CureWell Distributors", "date", "2026-10-23",
                        "items", 2, "total", 6400.0, "status", "Draft", "createdBy", "admin"))));

        data.put("sales", new ArrayList<>(List.of(
                row(1, "invoice", "INV-20261023-0001", "date", "2026-10-23 10:42", "customer", "Walk-in",
                        "items", 2, "subtotal", 85.0, "discount", 0.0, "tax", 0.0, "total", 85.0,
                        "status", "Confirmed", "createdBy", "pharmacist"),
                row(2, "invoice", "INV-20261023-0002", "date", "2026-10-23 11:05", "customer", "Anitha R.",
                        "items", 1, "subtotal", 24.6, "discount", 1.23, "tax", 0.0, "total", 23.37,
                        "status", "Confirmed", "createdBy", "pharmacist"))));

        data.put("customers", new ArrayList<>(List.of(
                row(1, "name", "Anitha R.", "phone", "+91 94444 01010", "email", "anitha@example.test",
                        "address", "Chennai", "active", "Active"),
                row(2, "name", "Karthik P.", "phone", "+91 95555 02020", "email", "karthik@example.test",
                        "address", "Chennai", "active", "Active"))));

        data.put("prescriptions", new ArrayList<>(List.of(
                row(1, "reference", "RX-CH-1009", "customer", "Anitha R.", "prescriber", "Dr. Sample",
                        "issue", "2026-10-20", "valid", "2026-11-20", "status", "Pending", "reviewer", "—"),
                row(2, "reference", "RX-CH-0944", "customer", "Karthik P.", "prescriber", "Dr. Sample",
                        "issue", "2026-10-15", "valid", "2026-10-30", "status", "Verified", "reviewer", "pharmacist"))));

        data.put("users", new ArrayList<>(List.of(
                row(1, "username", "admin", "role", "ADMIN", "status", "Active"),
                row(2, "username", "pharmacist", "role", "PHARMACIST", "status", "Active"),
                row(3, "username", "inventory", "role", "INVENTORY", "status", "Active"))));
    }

    /* ---------- calendar seed data ---------- */
    void seedEvents() {
        events.clear();
        addEvent("Stock audit — analgesics", TODAY, "09:00", "10:30", false, "General",
                "Pharmacy floor", "Quarterly count of analgesic shelves.");
        addEvent("NorthStar delivery", TODAY, "11:00", "12:00", false, "Deliveries",
                "Loading bay", "PO-20261022-A12 receipt and batch entry.");
        addEvent("Prescription review", TODAY, "15:00", "16:00", false, "General",
                "Counter 2", "Verify pending prescriptions.");
        addEvent("Cold-chain check", TODAY, "17:30", "18:00", false, "Reminders",
                "Fridge unit", "Log min/max temperature.");
        addEvent("Health camp — Chennai", LocalDate.of(2026, 10, 26), "08:00", "13:00", false, "Health camp",
                "Community hall", "Free BP and sugar screening.");
        addEvent("Supplier call — CureWell", LocalDate.of(2026, 10, 27), "10:00", "10:30", false, "General",
                "Phone", "Confirm January indent.");
        addEvent("Monthly stock close", LocalDate.of(2026, 10, 31), "00:00", "23:59", true, "Reminders",
                "", "Freeze stock movements for October.");
        addEvent("Expiry sweep", LocalDate.of(2026, 11, 2), "09:30", "11:00", false, "Deliveries",
                "Store room", "Pull batches expiring within 90 days.");
        addEvent("Team training — billing", LocalDate.of(2026, 11, 5), "16:00", "17:30", false, "General",
                "Training room", "GST invoicing refresher.");
    }

    void addEvent(String title, LocalDate d, String s, String e, boolean allDay, String cal, String loc, String notes) {
        CalEvent ev = new CalEvent();
        ev.id = System.nanoTime() + (long) (Math.random() * 100000);
        ev.title = title; ev.date = d; ev.allDay = allDay;
        ev.start = allDay ? null : LocalTime.parse(s);
        ev.end = allDay ? null : LocalTime.parse(e);
        ev.cal = cal; ev.location = loc; ev.notes = notes;
        events.add(ev);
    }

    /* ================================================================
     *  UI SKELETON
     * ================================================================ */
    void buildUi() {
        frame = new JFrame("MediStock — Pharmacy Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1460, 960);
        frame.setMinimumSize(new Dimension(1140, 720));
        frame.setLocationRelativeTo(null);
        installContent();
        frame.setVisible(true);
    }

    void installContent() {
        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().setBackground(PAPER);
        frame.getContentPane().add(buildSidebar(), BorderLayout.WEST);
        frame.getContentPane().add(buildMain(), BorderLayout.CENTER);
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
    }

    void rebuildUi() {
        installContent();
        render();
        toast("Theme applied: " + THEME.name);
    }

    JComponent buildSidebar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Color bottom = THEME.glow ? mix(NAV, GLOW, 0.20) : mix(NAV, NAV_HOVER, 0.55);
                g2.setPaint(new GradientPaint(0, 0, NAV, 0, getHeight(), bottom));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        side.setOpaque(false);
        side.setLayout(new VStackLayout(0));
        side.setPreferredSize(new Dimension(238, 0));
        side.setBorder(new EmptyBorder(24, 15, 20, 15));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);

        JLabel mark = new JLabel("M", SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, mix(BLUE, Color.WHITE, 0.25), getWidth(), getHeight(), BLUE_DARK));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mark.setPreferredSize(new Dimension(35, 35));
        mark.setForeground(Color.WHITE);
        mark.setFont(f(18, Font.BOLD));
        brand.add(mark);

        JLabel brandName = new JLabel("MediStock");
        brandName.setForeground(Color.WHITE);
        brandName.setFont(f(19, Font.BOLD));
        brand.add(brandName);

        side.add(brand);
        side.add(Box.createVerticalStrut(26));

        JLabel group = new JLabel("WORKSPACE");
        group.setForeground(NAV_MUTED);
        group.setFont(f(10, Font.BOLD));
        group.setBorder(new EmptyBorder(0, 12, 8, 0));
        side.add(group);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new VStackLayout(4));

        for (String[] item : NAV_ITEMS) {
            RoundButton b = new RoundButton(item[1] + "   " + item[2], null, NAV_HOVER, NAV_MUTED, null, 10);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setFont(f(13, Font.BOLD));
            b.addActionListener(e -> { current = item[0]; render(); });
            navButtons.put(item[0], b);
            nav.add(b);
        }
        side.add(nav);
        side.add(Box.createVerticalGlue());

        JPanel foot = new JPanel();
        foot.setOpaque(false);
        foot.setLayout(new VStackLayout(6));
        foot.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, mix(NAV, Color.WHITE, 0.14)),
                new EmptyBorder(16, 10, 0, 10)));

        JLabel fl1 = new JLabel("Standalone UI preview");
        fl1.setForeground(NAV_MUTED); fl1.setFont(f(11, Font.PLAIN));
        JLabel fl2 = new JLabel("Data is held in memory for this session.");
        fl2.setForeground(NAV_MUTED); fl2.setFont(f(11, Font.PLAIN));

        RoundButton themes = new RoundButton("🎨  Change theme", null, mix(NAV, Color.WHITE, 0.12),
                NAV_MUTED, mix(NAV, Color.WHITE, 0.22), 8);
        themes.setFont(f(11, Font.BOLD));
        themes.addActionListener(e -> openThemeDialog());

        RoundButton reset = new RoundButton("Reset preview data", null, mix(NAV, Color.WHITE, 0.12),
                NAV_MUTED, mix(NAV, Color.WHITE, 0.22), 8);
        reset.setFont(f(11, Font.BOLD));
        reset.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(frame, "Reset the preview to its original sample records?",
                    "Reset preview data", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                seedData(); seedEvents();
                calMonth = TODAY.withDayOfMonth(1); calSelected = TODAY;
                render(); updateBell(); toast("Preview data reset.");
            }
        });

        foot.add(fl1); foot.add(fl2); foot.add(themes); foot.add(reset);
        side.add(foot);
        return side;
    }

    JComponent buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(PAPER);
        main.add(buildTopBar(), BorderLayout.NORTH);

        page = new PagePanel();
        page.setBorder(new EmptyBorder(26, 34, 40, 34));

        JPanel head = new JPanel(new BorderLayout(20, 0));
        head.setOpaque(false);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new VStackLayout(6));

        JLabel kicker = new JLabel("PHARMACY MANAGEMENT SYSTEM");
        kicker.setForeground(BLUE);
        kicker.setFont(f(10, Font.BOLD));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setForeground(INK);
        pageTitle.setFont(f(29, Font.BOLD));

        pageSubtitle = new JLabel(" ");
        pageSubtitle.setForeground(MUTED);
        pageSubtitle.setFont(f(13, Font.PLAIN));

        textBlock.add(kicker);
        textBlock.add(pageTitle);
        textBlock.add(pageSubtitle);

        headActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headActions.setOpaque(false);

        head.add(textBlock, BorderLayout.CENTER);
        head.add(headActions, BorderLayout.EAST);

        viewHolder = new JPanel();
        viewHolder.setOpaque(false);
        viewHolder.setLayout(new VStackLayout(0));

        page.add(head);
        page.add(viewHolder);

        JLabel footer = new JLabel("<html>MediStock · Educational Swing preview. "
                + "This build does not connect to PostgreSQL or implement regulated dispensing.</html>");
        footer.setForeground(MUTED);
        footer.setFont(f(11, Font.PLAIN));
        page.add(footer);

        JScrollPane sp = new JScrollPane(page);
        sp.setBorder(null);
        sp.setBackground(PAPER);
        sp.getViewport().setBackground(PAPER);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        main.add(sp, BorderLayout.CENTER);
        return main;
    }

    JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(TOPBAR);
        top.setPreferredSize(new Dimension(0, 70));
        top.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, LINE),
                new EmptyBorder(0, 34, 0, 34)));

        crumb = new JLabel("Pharmacy operations / Dashboard");
        crumb.setForeground(MUTED);
        crumb.setFont(f(12, Font.PLAIN));
        top.add(crumb, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        bellBtn = outlineButton("🔔  Alerts");
        bellBtn.setFont(f(11, Font.BOLD));
        bellBtn.addActionListener(e -> showAlertsDialog());

        themeBtn = outlineButton("🎨  " + THEME.name);
        themeBtn.setFont(f(11, Font.BOLD));
        themeBtn.addActionListener(e -> openThemeDialog());

        JLabel avatar = new JLabel("A", SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(mix(BLUE, CARD, 0.85));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(33, 33));
        avatar.setForeground(BLUE_DARK);
        avatar.setFont(f(14, Font.BOLD));

        JLabel who = new JLabel("<html><b>Demo Admin</b><br>"
                + "<span style='color:" + hex(MUTED) + ";font-size:9px'>Administrator · preview mode</span></html>");
        who.setForeground(INK);
        who.setFont(f(12, Font.BOLD));

        right.add(bellBtn);
        right.add(themeBtn);
        right.add(avatar);
        right.add(who);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    /* ================================================================
     *  RENDER
     * ================================================================ */
    void render() {
        Module m = modules.get(current);
        String crumbText;
        String sub;

        if (current.equals("dashboard")) {
            crumbText = "Dashboard";
            sub = "Operational overview · live clock, focus timer and expiry reminders.";
        } else if (current.equals("reports")) {
            crumbText = "Reports & alerts";
            sub = "Period summaries, inventory alerts and auditable stock changes.";
        } else if (current.equals("calendar")) {
            crumbText = "Calendar";
            sub = "Google-Calendar style scheduling: month grid, agenda, colours and all-day events.";
        } else {
            crumbText = m.title;
            sub = m.subtitle;
        }

        crumb.setText("Pharmacy operations / " + crumbText);
        pageTitle.setText(crumbText);
        pageSubtitle.setText(sub);

        for (Map.Entry<String, RoundButton> e : navButtons.entrySet())
            e.getValue().setActiveState(e.getKey().equals(current));

        headActions.removeAll();
        if (m != null && m.button != null) {
            RoundButton add = primaryButton("＋ " + m.button);
            add.addActionListener(e -> openRecordDialog(current, null));
            headActions.add(add);
        } else if (current.equals("calendar")) {
            RoundButton add = primaryButton("＋ New event");
            add.addActionListener(e -> openEventDialog(null, calSelected));
            headActions.add(add);
        }
        headActions.revalidate();
        headActions.repaint();

        viewHolder.removeAll();
        JComponent view;
        if (current.equals("dashboard")) view = dashboardView();
        else if (current.equals("reports")) view = reportsView();
        else if (current.equals("calendar")) view = calendarView();
        else view = moduleView(current);
        viewHolder.add(view);
        viewHolder.revalidate();
        viewHolder.repaint();

        updateBell();
        page.revalidate();
        page.repaint();
    }

    void updateBell() {
        if (bellBtn == null) return;
        int n = computeExpiryAlerts().size();
        bellBtn.setText(n == 0 ? "🔔  No alerts" : "🔔  " + n + " alert" + (n == 1 ? "" : "s"));
        bellBtn.setForeground(n == 0 ? MUTED : RED);
        if (themeBtn != null) themeBtn.setText("🎨  " + THEME.name);
    }

    /* ================================================================
     *  EXPIRY REMINDERS
     * ================================================================ */
    List<ExpiryAlert> computeExpiryAlerts() {
        List<ExpiryAlert> out = new ArrayList<>();

        for (Row m : data.get("medicines")) {
            LocalDate d = parseDate(m.s("expiry"));
            if (d == null) continue;
            long days = ChronoUnit.DAYS.between(TODAY, d);
            if (days <= 30) {
                ExpiryAlert a = new ExpiryAlert();
                a.medicine = m.s("name");
                a.batch = "";
                a.expiry = m.s("expiry");
                a.days = days;
                a.expired = days < 0;
                a.batchLevel = false;
                out.add(a);
            }
        }
        for (Row b : data.get("inventory")) {
            LocalDate d = parseDate(b.s("expiry"));
            if (d == null || b.d("qty") <= 0) continue;
            long days = ChronoUnit.DAYS.between(TODAY, d);
            if (days <= 30) {
                ExpiryAlert a = new ExpiryAlert();
                a.medicine = b.s("medicine");
                a.batch = b.s("batch");
                a.expiry = b.s("expiry");
                a.days = days;
                a.expired = days < 0;
                a.batchLevel = true;
                out.add(a);
            }
        }
        out.sort((a, b) -> Long.compare(a.days, b.days));
        return out;
    }

    /** Returns a warning string when the record being saved needs an expiry reminder. */
    String expiryMessage(String moduleKey, Row row) {
        if (!moduleKey.equals("medicines") && !moduleKey.equals("inventory")) return null;
        String exp = row.s("expiry").trim();
        if (exp.isEmpty()) return null;
        LocalDate d = parseDate(exp);
        if (d == null) return "⚠ The expiry date “" + exp + "” is not a valid YYYY-MM-DD date.";

        long days = ChronoUnit.DAYS.between(TODAY, d);
        if (days < 0)
            return "⛔ EXPIRED: this item expired " + Math.abs(days) + " day(s) ago (" + exp + ").\n"
                    + "It must not be dispensed — move it to the quarantine bin.";
        if (days == 0)
            return "⛔ EXPIRES TODAY (" + exp + ").\nRemove it from the dispensing shelf and quarantine the stock.";
        if (days <= 30)
            return "⚠ NEAR EXPIRY: only " + days + " day(s) left (" + exp + ").\n"
                    + "Plan a supplier return, a stock rotation or a discounted clearance.";
        return null;
    }

    void showExpiryAlert(String message) {
        boolean critical = message.startsWith("⛔");
        Color accent = critical ? RED : AMBER;

        JDialog dlg = new JDialog(frame, true);
        dlg.setTitle("Expiry reminder");

        JPanel root = new JPanel();
        root.setBackground(CARD);
        root.setLayout(new VStackLayout(0));
        root.setBorder(new EmptyBorder(22, 22, 18, 22));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        header.setOpaque(false);
        JLabel icon = new JLabel(critical ? "⛔" : "⚠");
        icon.setFont(f(26, Font.BOLD));
        icon.setForeground(accent);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new VStackLayout(2));
        JLabel t1 = new JLabel(critical ? "Expired medicine detected" : "Expiry warning");
        t1.setFont(f(19, Font.BOLD));
        t1.setForeground(INK);
        JLabel t2 = new JLabel("Stock must never be dispensed past its expiry date.");
        t2.setFont(f(11, Font.PLAIN));
        t2.setForeground(MUTED);
        titles.add(t1); titles.add(t2);
        header.add(icon);
        header.add(titles);

        JLabel body = new JLabel("<html><div style='width:380px'>"
                + esc(message).replace("\n", "<br>") + "</div></html>");
        body.setFont(f(13, Font.PLAIN));
        body.setForeground(INK);
        body.setBorder(new CompoundBorder(new RoundBorder(10, tintBg(accent), mix(accent, CARD, 0.65)),
                new EmptyBorder(12, 14, 12, 14)));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(16, 0, 0, 0));
        RoundButton ok = primaryButton("Understood");
        ok.addActionListener(e -> dlg.dispose());
        actions.add(ok);

        root.add(header);
        root.add(Box.createVerticalStrut(14));
        root.add(body);
        root.add(actions);

        dlg.setContentPane(root);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    void showAlertsDialog() {
        List<ExpiryAlert> alerts = computeExpiryAlerts();

        JDialog dlg = new JDialog(frame, true);
        dlg.setTitle("Expiry reminders");

        JPanel root = new JPanel();
        root.setBackground(CARD);
        root.setLayout(new VStackLayout(0));
        root.setBorder(new EmptyBorder(22, 22, 18, 22));

        JLabel t = new JLabel("Expiry reminders");
        t.setFont(f(21, Font.BOLD));
        t.setForeground(INK);
        JLabel sub = new JLabel(alerts.isEmpty()
                ? "Everything is inside the safe window."
                : alerts.size() + " item(s) expire within 30 days of the demo date (" + TODAY + ").");
        sub.setFont(f(12, Font.PLAIN));
        sub.setForeground(MUTED);
        sub.setBorder(new EmptyBorder(6, 0, 16, 0));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new VStackLayout(8));

        if (alerts.isEmpty()) {
            JLabel empty = new JLabel("No expiring or expired stock right now.");
            empty.setFont(f(12, Font.PLAIN));
            empty.setForeground(MUTED);
            empty.setBorder(new EmptyBorder(10, 0, 10, 0));
            list.add(empty);
        } else {
            int shown = 0;
            for (ExpiryAlert a : alerts) {
                if (shown++ >= 8) break;
                list.add(alertRow(a));
            }
        }

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        sp.getViewport().setBackground(CARD);
        sp.setPreferredSize(new Dimension(520, Math.min(360, Math.max(120, list.getPreferredSize().height + 20))));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(14, 0, 0, 0));
        RoundButton open = outlineButton("Open batch inventory");
        open.addActionListener(e -> { dlg.dispose(); current = "inventory"; render(); });
        RoundButton close = primaryButton("Close");
        close.addActionListener(e -> dlg.dispose());
        actions.add(open);
        actions.add(close);

        root.add(t);
        root.add(sub);
        root.add(sp);
        root.add(actions);

        dlg.setContentPane(root);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    JComponent alertRow(ExpiryAlert a) {
        Color accent = a.expired ? RED : AMBER;

        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(new CompoundBorder(new RoundBorder(10, tintBg(accent), mix(accent, CARD, 0.72)),
                new EmptyBorder(9, 11, 9, 11)));

        JLabel bar = new JLabel();
        bar.setOpaque(true);
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(4, 0));
        p.add(bar, BorderLayout.WEST);

        String detail = a.expired
                ? "expired " + Math.abs(a.days) + " day(s) ago"
                : a.days == 0 ? "expires today" : a.days + " day(s) left";

        JLabel text = new JLabel("<html><b>" + esc(a.medicine) + "</b>"
                + (a.batchLevel ? " · batch " + esc(a.batch) : " · catalogue")
                + "<br><span style='color:" + hex(MUTED) + ";font-size:9px'>" + esc(a.expiry)
                + " · " + detail + "</span></html>");
        text.setForeground(INK);
        text.setFont(f(12, Font.BOLD));
        p.add(text, BorderLayout.CENTER);

        Pill pill = new Pill();
        pill.setText(a.expired ? "Expired" : "Near expiry");
        Color[] st = badgeStyle(a.expired ? "Expired" : "Low");
        pill.setPill(st[0], st[1]);
        p.add(pill, BorderLayout.EAST);
        return p;
    }

    /* ================================================================
     *  VIEW: DASHBOARD
     * ================================================================ */
    JComponent dashboardView() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new VStackLayout(18));

        // ---- note banner
        JPanel note = new JPanel(new BorderLayout());
        note.setOpaque(false);
        note.setBorder(new CompoundBorder(new RoundBorder(10, tintBg(BLUE), mix(BLUE, CARD, 0.7)),
                new EmptyBorder(11, 13, 11, 13)));
        JLabel nl = new JLabel("<html><b>Preview mode:</b> this Swing build uses in-memory sample records. "
                + "Theme, expiry reminders and the focus timer are all local to this session.</html>");
        nl.setForeground(tintText(BLUE));
        nl.setFont(f(12, Font.PLAIN));
        note.add(nl, BorderLayout.CENTER);
        root.add(note);

        // ---- metrics
        List<Row> meds = data.get("medicines");
        List<Row> inv = data.get("inventory");
        List<Row> sales = data.get("sales");
        List<Row> purchases = data.get("purchases");
        List<Row> prescriptions = data.get("prescriptions");

        int low = 0;
        for (Row r : meds) if (r.d("stock") <= r.d("reorder")) low++;

        int expired = 0, near = 0, units = 0;
        for (Row b : inv) {
            units += (int) b.d("qty");
            LocalDate exp = parseDate(b.s("expiry"));
            if (exp == null) continue;
            if (exp.isBefore(TODAY) && b.d("qty") > 0) expired++;
            else if (!exp.isBefore(TODAY) && !exp.isAfter(TODAY.plusDays(30)) && b.d("qty") > 0) near++;
        }

        int todaysCount = 0;
        double salesTotal = 0;
        for (Row s : sales) {
            if (s.s("date").startsWith(TODAY.toString()) && s.s("status").equals("Confirmed")) {
                todaysCount++;
                salesTotal += s.d("total");
            }
        }

        int draft = 0;
        for (Row p : purchases) if (p.s("status").equals("Draft")) draft++;
        int pending = 0;
        for (Row p : prescriptions) if (p.s("status").equals("Pending")) pending++;
        int activeSuppliers = 0;
        for (Row s : data.get("suppliers")) if (s.s("active").equals("Active")) activeSuppliers++;

        JPanel row1 = grid(1, 4, 14, 14);
        row1.add(statCard("Active medicines", String.valueOf(meds.size()), "Catalogue products", "Rx", BLUE));
        row1.add(statCard("Available stock", String.valueOf(units), "Total units across demo batches", "＋", GREEN));
        row1.add(statCard("Low stock", String.valueOf(low), "At or below reorder threshold", "!", AMBER));
        row1.add(statCard("Near expiry / expired", String.valueOf(near + expired),
                near + " near expiry · " + expired + " expired", "◷", RED));
        root.add(row1);

        JPanel row2 = grid(1, 4, 14, 14);
        row2.add(statCard("Today's sales", money(salesTotal), todaysCount + " confirmed invoices", "₹", BLUE));
        row2.add(statCard("Draft purchases", String.valueOf(draft), "Waiting to be received", "PO", BLUE));
        row2.add(statCard("Pending prescriptions", String.valueOf(pending), "Require professional review", "Rx", AMBER));
        row2.add(statCard("Suppliers", String.valueOf(activeSuppliers), "Active suppliers", "◈", GREEN));
        root.add(row2);

        // ---- NEW: focus timer + expiry reminders
        root.add(split(timerCard(), expiryCard(), 1.0, 1.0));

        // ---- inventory watch
        List<Row> watch = new ArrayList<>();
        for (Row b : inv) if (b.s("expiry").compareTo("2027-02-28") <= 0) watch.add(b);
        if (watch.size() > 5) watch = new ArrayList<>(watch.subList(0, 5));

        List<String[]> wcols = List.of(
                new String[]{"medicine", "Medicine"},
                new String[]{"batch", "Batch"},
                new String[]{"expiry", "Expiry"},
                new String[]{"qty", "Available qty"},
                new String[]{"supplier", "Supplier"});

        JPanel watchCard = cardPanel();
        RoundButton open = outlineButton("Open inventory →");
        open.addActionListener(e -> { current = "inventory"; render(); });
        watchCard.add(sectionHead("Inventory watch", open), BorderLayout.NORTH);
        watchCard.add(tableScroll("inventory", wcols, watch, true), BorderLayout.CENTER);

        JPanel quickCard = cardPanel();
        quickCard.add(sectionHead("Quick actions", null), BorderLayout.NORTH);
        JPanel quick = new JPanel(new GridLayout(2, 2, 9, 9));
        quick.setOpaque(false);
        quick.add(quickAction("＋ Add medicine", "Catalogue definitions", "medicines"));
        quick.add(quickAction("＋ New purchase", "Manage supplier stock receipts", "purchases"));
        quick.add(quickAction("＋ New sale", "Preview invoice records", "sales"));
        quick.add(quickAction("🎨 Theme settings", "12 looks incl. Matrix & Cyberpunk", "__theme"));
        JPanel quickWrap = new JPanel();
        quickWrap.setOpaque(false);
        quickWrap.setLayout(new VStackLayout(12));
        quickWrap.add(quick);
        JLabel hint = new JLabel("<html>Tax, prescription and dispensing rules must be verified for the "
                + "deployment location before real use.</html>");
        hint.setForeground(MUTED);
        hint.setFont(f(11, Font.PLAIN));
        quickWrap.add(hint);
        quickCard.add(quickWrap, BorderLayout.CENTER);

        root.add(split(watchCard, quickCard, 1.6, 1.0));
        return root;
    }

    JPanel timerCard() {
        JPanel card = cardPanel();
        card.add(sectionHead("Live clock & focus timer", null), BorderLayout.NORTH);
        card.add(new TimerWidget(), BorderLayout.CENTER);
        return card;
    }

    JPanel expiryCard() {
        JPanel card = cardPanel();
        RoundButton all = outlineButton("View all →");
        all.addActionListener(e -> showAlertsDialog());
        card.add(sectionHead("Expiry reminders", all), BorderLayout.NORTH);

        List<ExpiryAlert> alerts = computeExpiryAlerts();
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new VStackLayout(8));

        if (alerts.isEmpty()) {
            JLabel ok = new JLabel("All batches are inside the safe window (30 days).");
            ok.setFont(f(12, Font.PLAIN));
            ok.setForeground(MUTED);
            ok.setBorder(new EmptyBorder(8, 0, 8, 0));
            list.add(ok);
        } else {
            int shown = 0;
            for (ExpiryAlert a : alerts) {
                if (shown++ >= 4) break;
                list.add(alertRow(a));
            }
            if (alerts.size() > 4) {
                JLabel more = new JLabel("+" + (alerts.size() - 4) + " more expiring item(s)");
                more.setFont(f(11, Font.BOLD));
                more.setForeground(MUTED);
                list.add(more);
            }
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    /* ================================================================
     *  TIMER WIDGET (live clock + focus countdown + session data)
     * ================================================================ */
    class TimerWidget extends JPanel {
        final JLabel clock = new JLabel("--:--:--");
        final JLabel dateL = new JLabel(" ");
        final JLabel countdown = new JLabel("10:00");
        final JLabel stateL = new JLabel("Ready");
        final JLabel session = new JLabel(" ");
        final Bar bar = new Bar();
        final RoundButton startBtn;
        final LocalTime started = LocalTime.now();
        final javax.swing.Timer tick;

        int preset = 10;
        long remaining = 10L * 60 * 1000;
        long endAt = 0;
        boolean running = false;
        int completed = 0;

        TimerWidget() {
            setOpaque(false);
            setLayout(new VStackLayout(10));
            setBorder(new EmptyBorder(2, 2, 2, 2));

            // live clock
            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);
            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new VStackLayout(2));
            JLabel cap = new JLabel("LOCAL TIME");
            cap.setFont(f(10, Font.BOLD));
            cap.setForeground(BLUE);
            clock.setFont(f(28, Font.BOLD));
            clock.setForeground(INK);
            dateL.setFont(f(11, Font.PLAIN));
            dateL.setForeground(MUTED);
            left.add(cap);
            left.add(clock);
            left.add(dateL);
            topRow.add(left, BorderLayout.WEST);

            Pill themePill = new Pill();
            themePill.setText(THEME.name);
            themePill.setPill(tintBg(GLOW), tintText(GLOW));
            topRow.add(themePill, BorderLayout.EAST);
            add(topRow);

            // focus countdown
            JPanel mid = new JPanel();
            mid.setOpaque(false);
            mid.setLayout(new VStackLayout(6));
            JLabel fcap = new JLabel("FOCUS TIMER");
            fcap.setFont(f(10, Font.BOLD));
            fcap.setForeground(GREEN);

            JPanel numRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            numRow.setOpaque(false);
            countdown.setFont(f(38, Font.BOLD));
            countdown.setForeground(INK);
            stateL.setFont(f(11, Font.BOLD));
            stateL.setForeground(MUTED);
            numRow.add(countdown);
            numRow.add(stateL);

            bar.setPreferredSize(new Dimension(200, 8));
            mid.add(fcap);
            mid.add(numRow);
            mid.add(bar);
            add(mid);

            // presets + controls
            JPanel bottom = new JPanel(new BorderLayout(8, 0));
            bottom.setOpaque(false);

            JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            chips.setOpaque(false);
            for (int mnt : new int[]{5, 10, 15, 25}) {
                RoundButton b = new RoundButton(mnt + "m",
                        mix(BLUE, CARD, THEME.dark ? 0.72 : 0.88), mix(BLUE, CARD, 0.74),
                        BLUE, null, 8);
                b.setFont(f(11, Font.BOLD));
                b.addActionListener(e -> {
                    preset = mnt;
                    if (!running) { remaining = mnt * 60000L; }
                    refresh();
                });
                chips.add(b);
            }

            JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            ctrl.setOpaque(false);
            startBtn = primaryButton("Start");
            RoundButton resetBtn = outlineButton("Reset");

            startBtn.addActionListener(e -> {
                if (running) {
                    remaining = Math.max(0, endAt - System.currentTimeMillis());
                    running = false;
                    startBtn.setText("Start");
                } else {
                    if (remaining <= 0) remaining = preset * 60000L;
                    endAt = System.currentTimeMillis() + remaining;
                    running = true;
                    startBtn.setText("Pause");
                }
                refresh();
            });
            resetBtn.addActionListener(e -> {
                running = false;
                startBtn.setText("Start");
                remaining = preset * 60000L;
                refresh();
            });

            ctrl.add(startBtn);
            ctrl.add(resetBtn);
            bottom.add(chips, BorderLayout.WEST);
            bottom.add(ctrl, BorderLayout.EAST);
            add(bottom);

            session.setFont(f(11, Font.PLAIN));
            session.setForeground(MUTED);
            add(session);

            tick = new javax.swing.Timer(250, e -> onTick());
            tick.start();
            refresh();
        }

        @Override public void removeNotify() {
            tick.stop();
            super.removeNotify();
        }

        void onTick() {
            LocalTime now = LocalTime.now();
            clock.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            dateL.setText(now.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)));

            if (running) {
                long left = endAt - System.currentTimeMillis();
                if (left <= 0) {
                    remaining = 0;
                    running = false;
                    completed++;
                    startBtn.setText("Start");
                    toast("Focus timer finished — " + preset + " minute block complete.");
                    try { Toolkit.getDefaultToolkit().beep(); } catch (Exception ignored) { }
                } else {
                    remaining = left;
                }
            }
            refresh();
        }

        void refresh() {
            long total = Math.max(1, preset * 60000L);
            long sec = Math.max(0, (remaining + 999) / 1000);
            countdown.setText(String.format(Locale.US, "%02d:%02d", sec / 60, sec % 60));
            bar.pct = Math.max(0, Math.min(1, remaining / (double) total));
            bar.repaint();

            if (running) stateL.setText("Focusing");
            else if (remaining <= 0) stateL.setText("Done");
            else if (remaining < total) stateL.setText("Paused");
            else stateL.setText("Ready");
            stateL.setForeground(running ? GREEN : MUTED);

            session.setText("Completed " + completed + " · session started "
                    + started.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    /* ================================================================
     *  VIEW: MODULE TABLE
     * ================================================================ */
    JComponent moduleView(String key) {
        Module m = modules.get(key);
        List<Row> rows = data.get(key);

        JPanel card = cardPanel();

        JLabel title = new JLabel(m.title + "   (" + rows.size() + " sample records)");
        title.setFont(f(16, Font.BOLD));
        title.setForeground(INK);

        JTextField search = new JTextField(16);
        search.setPreferredSize(new Dimension(240, 34));
        search.setFont(f(12, Font.PLAIN));
        search.setBackground(CARD2);
        search.setForeground(INK);
        search.setCaretColor(INK);
        search.setBorder(new CompoundBorder(new RoundBorder(9, CARD2, LINE), new EmptyBorder(6, 10, 6, 10)));

        RoundButton clear = outlineButton("Clear");
        clear.setFont(f(11, Font.BOLD));

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBox.setOpaque(false);
        searchBox.add(search);
        searchBox.add(clear);

        JPanel head = new JPanel(new BorderLayout(12, 0));
        head.setOpaque(false);
        head.add(title, BorderLayout.WEST);
        head.add(searchBox, BorderLayout.EAST);

        JScrollPane sp = tableScroll(key, m.cols, rows, true);
        JTable table = (JTable) sp.getViewport().getView();
        if (table != null) {
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
            table.setRowSorter(sorter);
            Runnable apply = () -> {
                String t = search.getText().trim();
                if (t.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(t)));
            };
            search.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { apply.run(); }
                public void removeUpdate(DocumentEvent e) { apply.run(); }
                public void changedUpdate(DocumentEvent e) { apply.run(); }
            });
            clear.addActionListener(e -> search.setText(""));
        }

        JLabel hint = new JLabel("In-memory preview: records reset when the application closes. "
                + "Expiry dates trigger reminders when you save.");
        hint.setForeground(MUTED);
        hint.setFont(f(11, Font.PLAIN));
        hint.setBorder(new EmptyBorder(12, 0, 0, 0));

        card.add(head, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(hint, BorderLayout.SOUTH);
        return card;
    }

    /* ================================================================
     *  VIEW: REPORTS
     * ================================================================ */
    JComponent reportsView() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new VStackLayout(18));

        List<Row> meds = data.get("medicines");
        List<Row> inv = data.get("inventory");

        List<Row> low = new ArrayList<>();
        for (Row m : meds) if (m.d("stock") <= m.d("reorder")) low.add(m);

        List<Row> near = new ArrayList<>(), expired = new ArrayList<>();
        for (Row b : inv) {
            LocalDate exp = parseDate(b.s("expiry"));
            if (exp == null || b.d("qty") <= 0) continue;
            if (exp.isBefore(TODAY)) expired.add(b);
            else if (!exp.isAfter(TODAY.plusDays(30))) near.add(b);
        }

        double total = 0;
        int count = 0;
        for (Row s : data.get("sales")) {
            if (s.s("date").startsWith(TODAY.toString()) && s.s("status").equals("Confirmed")) {
                total += s.d("total");
                count++;
            }
        }

        JPanel stats = grid(1, 4, 14, 14);
        stats.add(statCard("Sales today", money(total), count + " confirmed invoices", "₹", BLUE));
        stats.add(statCard("Low stock", String.valueOf(low.size()), "Medicines below configured levels", "!", BLUE));
        stats.add(statCard("Near expiry", String.valueOf(near.size()), "Within 30 days (demo date)", "◷", AMBER));
        stats.add(statCard("Expired stock", String.valueOf(expired.size()), "Must not be dispensed", "×", RED));
        root.add(stats);

        List<String[]> lowCols = List.of(
                new String[]{"name", "Medicine"},
                new String[]{"sku", "SKU"},
                new String[]{"stock", "Stock"},
                new String[]{"reorder", "Reorder at"});

        List<String[]> expCols = List.of(
                new String[]{"medicine", "Medicine"},
                new String[]{"batch", "Batch"},
                new String[]{"expiry", "Expiry"},
                new String[]{"qty", "Available qty"});

        List<Row> nearAll = new ArrayList<>(near);
        nearAll.addAll(expired);

        JPanel lowCard = cardPanel();
        lowCard.add(sectionHead("Low-stock items", null), BorderLayout.NORTH);
        lowCard.add(tableScroll("medicines", lowCols, low, true), BorderLayout.CENTER);

        JPanel expCard = cardPanel();
        expCard.add(sectionHead("Near expiry / expired batches", null), BorderLayout.NORTH);
        expCard.add(tableScroll("inventory", expCols, nearAll, true), BorderLayout.CENTER);

        root.add(split(lowCard, expCard, 1.0, 1.0));

        List<String[]> salesCols = List.of(
                new String[]{"invoice", "Invoice"},
                new String[]{"date", "Date"},
                new String[]{"customer", "Customer"},
                new String[]{"status", "Status"},
                new String[]{"total", "Total"});

        JPanel salesCard = cardPanel();
        salesCard.add(sectionHead("Sales report", null), BorderLayout.NORTH);
        salesCard.add(tableScroll("sales", salesCols, data.get("sales"), true), BorderLayout.CENTER);
        root.add(salesCard);

        JPanel note = new JPanel(new BorderLayout());
        note.setOpaque(false);
        note.setBorder(new CompoundBorder(new RoundBorder(10, tintBg(BLUE), mix(BLUE, CARD, 0.7)),
                new EmptyBorder(11, 13, 11, 13)));
        JLabel nl = new JLabel("<html>The server build adds database-backed date filters, CSV export, "
                + "stock-movement history and audit events. This preview uses a fixed demo date "
                + "(<b>" + TODAY + "</b>).</html>");
        nl.setForeground(tintText(BLUE));
        nl.setFont(f(12, Font.PLAIN));
        note.add(nl, BorderLayout.CENTER);
        root.add(note);
        return root;
    }

    /* ================================================================
     *  VIEW: CALENDAR (Google-Calendar inspired)
     * ================================================================ */
    JComponent calendarView() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new VStackLayout(18));

        JPanel monthCard = cardPanel();
        monthCard.setLayout(new BorderLayout(0, 14));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        RoundButton prev = outlineButton("<");
        prev.setPreferredSize(new Dimension(40, 34));
        prev.addActionListener(e -> { calMonth = calMonth.minusMonths(1); render(); });

        RoundButton todayBtn = outlineButton("Today");
        todayBtn.addActionListener(e -> { calMonth = TODAY.withDayOfMonth(1); calSelected = TODAY; render(); });

        RoundButton next = outlineButton(">");
        next.setPreferredSize(new Dimension(40, 34));
        next.addActionListener(e -> { calMonth = calMonth.plusMonths(1); render(); });

        JLabel monthLabel = new JLabel(calMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));
        monthLabel.setFont(f(18, Font.BOLD));
        monthLabel.setForeground(INK);
        monthLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        left.add(prev); left.add(todayBtn); left.add(next); left.add(monthLabel);
        toolbar.add(left, BorderLayout.WEST);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        legend.setOpaque(false);
        for (String[] c : CALENDARS) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            item.setOpaque(false);
            JLabel dot = new JLabel("●");
            dot.setForeground(Color.decode(c[1]));
            dot.setFont(f(11, Font.PLAIN));
            JLabel nm = new JLabel(c[0]);
            nm.setForeground(MUTED);
            nm.setFont(f(11, Font.PLAIN));
            item.add(dot); item.add(nm);
            legend.add(item);
        }
        toolbar.add(legend, BorderLayout.EAST);
        monthCard.add(toolbar, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(7, 7, 1, 1));
        grid.setBackground(LINE);
        grid.setPreferredSize(new Dimension(10, 7 * 96));

        String[] dow = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String d : dow) {
            JLabel l = new JLabel(d.toUpperCase(Locale.ENGLISH));
            l.setFont(f(10, Font.BOLD));
            l.setForeground(MUTED);
            l.setOpaque(true);
            l.setBackground(CARD2);
            l.setBorder(new EmptyBorder(0, 12, 0, 0));
            grid.add(l);
        }

        LocalDate first = calMonth.withDayOfMonth(1);
        int shift = first.getDayOfWeek().getValue() % 7;
        LocalDate cursor = first.minusDays(shift);

        for (int i = 0; i < 42; i++) {
            grid.add(dayCell(cursor));
            cursor = cursor.plusDays(1);
        }
        monthCard.add(grid, BorderLayout.CENTER);
        root.add(monthCard);

        JPanel agendaCard = cardPanel();
        agendaCard.add(sectionHead("Agenda · " +
                calSelected.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)), null),
                BorderLayout.NORTH);

        JPanel agendaList = new JPanel();
        agendaList.setOpaque(false);
        agendaList.setLayout(new VStackLayout(8));

        List<CalEvent> dayEvents = eventsOn(calSelected);
        if (dayEvents.isEmpty()) {
            JLabel empty = new JLabel("No events scheduled. Double-click a day in the grid to add one.");
            empty.setForeground(MUTED);
            empty.setFont(f(12, Font.PLAIN));
            empty.setBorder(new EmptyBorder(14, 0, 14, 0));
            agendaList.add(empty);
        } else {
            for (CalEvent ev : dayEvents) agendaList.add(eventRow(ev, true));
        }
        agendaCard.add(agendaList, BorderLayout.CENTER);

        JPanel upcomingCard = cardPanel();
        upcomingCard.add(sectionHead("Upcoming", null), BorderLayout.NORTH);

        JPanel upList = new JPanel();
        upList.setOpaque(false);
        upList.setLayout(new VStackLayout(8));

        List<CalEvent> upcoming = new ArrayList<>();
        for (CalEvent ev : events) if (!ev.date.isBefore(TODAY)) upcoming.add(ev);
        upcoming.sort(eventComparator());
        int shown = 0;
        for (CalEvent ev : upcoming) {
            if (shown++ >= 6) break;
            upList.add(eventRow(ev, false));
        }
        if (upcoming.isEmpty()) {
            JLabel empty = new JLabel("Nothing upcoming.");
            empty.setForeground(MUTED);
            empty.setFont(f(12, Font.PLAIN));
            upList.add(empty);
        }
        upcomingCard.add(upList, BorderLayout.CENTER);

        root.add(split(agendaCard, upcomingCard, 1.35, 1.0));
        return root;
    }

    JComponent dayCell(LocalDate date) {
        boolean inMonth = date.getMonthValue() == calMonth.getMonthValue() && date.getYear() == calMonth.getYear();
        boolean isToday = date.equals(TODAY);
        boolean isSelected = date.equals(calSelected);

        JPanel cell = new JPanel(new BorderLayout(0, 4));
        cell.setOpaque(true);
        cell.setBackground(isSelected ? mix(BLUE, CARD, 0.9) : inMonth ? CARD : mix(CARD, PAPER, 0.5));
        cell.setBorder(new CompoundBorder(
                isSelected ? new MatteBorder(1, 1, 1, 1, BLUE) : new EmptyBorder(1, 1, 1, 1),
                new EmptyBorder(5, 6, 5, 6)));
        cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel num = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                if (isToday) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BLUE);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        num.setPreferredSize(new Dimension(24, 24));
        num.setFont(f(11, Font.BOLD));
        num.setForeground(isToday ? Color.WHITE : inMonth ? INK : MUTED);

        JPanel numRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        numRow.setOpaque(false);
        numRow.add(num);
        cell.add(numRow, BorderLayout.NORTH);

        JPanel chips = new JPanel();
        chips.setOpaque(false);
        chips.setLayout(new VStackLayout(3));

        List<CalEvent> dayEvents = eventsOn(date);
        int max = 3;
        for (int i = 0; i < Math.min(max, dayEvents.size()); i++) chips.add(chip(dayEvents.get(i)));
        if (dayEvents.size() > max) {
            JLabel more = new JLabel("+" + (dayEvents.size() - max) + " more");
            more.setFont(f(9, Font.BOLD));
            more.setForeground(MUTED);
            chips.add(more);
        }
        cell.add(chips, BorderLayout.CENTER);

        cell.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openEventDialog(null, date);
                } else {
                    calSelected = date;
                    if (date.getMonthValue() != calMonth.getMonthValue() || date.getYear() != calMonth.getYear())
                        calMonth = date.withDayOfMonth(1);
                    render();
                }
            }
        });
        return cell;
    }

    JComponent chip(CalEvent ev) {
        Color base = Color.decode(colorFor(ev.cal));
        Color bg = mix(base, CARD, THEME.dark ? 0.72 : 0.86);

        String time = ev.allDay ? "" : ev.start.format(DateTimeFormatter.ofPattern("HH:mm")) + " ";
        String text = time + ev.title;
        if (text.length() > 24) text = text.substring(0, 23) + "…";

        JLabel chip = new JLabel(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, 3, getHeight(), 3, 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(f(9, Font.BOLD));
        chip.setForeground(THEME.dark ? mix(base, Color.WHITE, 0.55) : base.darker());
        chip.setBorder(new EmptyBorder(3, 7, 3, 5));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                e.consume();
                openEventDialog(ev, ev.date);
            }
        });
        return chip;
    }

    JComponent eventRow(CalEvent ev, boolean showDate) {
        Color base = Color.decode(colorFor(ev.cal));

        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(new CompoundBorder(new RoundBorder(10, CARD2, LINE), new EmptyBorder(9, 11, 9, 11)));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel bar = new JLabel();
        bar.setOpaque(true);
        bar.setBackground(base);
        bar.setPreferredSize(new Dimension(4, 0));
        p.add(bar, BorderLayout.WEST);

        String when = ev.allDay ? "All day"
                : ev.start.format(DateTimeFormatter.ofPattern("HH:mm")) + " – "
                  + ev.end.format(DateTimeFormatter.ofPattern("HH:mm"));
        String head = showDate
                ? "<b>" + esc(ev.title) + "</b><br><span style='color:" + hex(MUTED) + ";font-size:9px'>" + when + "</span>"
                : "<b>" + esc(ev.title) + "</b><br><span style='color:" + hex(MUTED) + ";font-size:9px'>"
                  + ev.date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)) + " · " + when + "</span>";

        JLabel t = new JLabel("<html>" + head + "</html>");
        t.setForeground(INK);
        t.setFont(f(12, Font.BOLD));
        p.add(t, BorderLayout.CENTER);

        if (!ev.location.isEmpty()) {
            JLabel loc = new JLabel("◉ " + esc(ev.location));
            loc.setForeground(MUTED);
            loc.setFont(f(10, Font.PLAIN));
            p.add(loc, BorderLayout.EAST);
        }

        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openEventDialog(ev, ev.date); }
        });
        return p;
    }

    List<CalEvent> eventsOn(LocalDate d) {
        List<CalEvent> out = new ArrayList<>();
        for (CalEvent ev : events) if (ev.date.equals(d)) out.add(ev);
        out.sort(eventComparator());
        return out;
    }

    Comparator<CalEvent> eventComparator() {
        return (a, b) -> {
            int c = a.date.compareTo(b.date);
            if (c != 0) return c;
            if (a.allDay && !b.allDay) return -1;
            if (!a.allDay && b.allDay) return 1;
            if (a.allDay) return a.title.compareToIgnoreCase(b.title);
            return a.start.compareTo(b.start);
        };
    }

    String colorFor(String cal) {
        for (String[] c : CALENDARS) if (c[0].equals(cal)) return c[1];
        return "#4568e8";
    }

    /* ================================================================
     *  THEME PICKER
     * ================================================================ */
    void openThemeDialog() {
        JDialog dlg = new JDialog(frame, true);
        dlg.setTitle("Theme settings");

        JPanel root = new JPanel();
        root.setBackground(CARD);
        root.setLayout(new VStackLayout(0));
        root.setBorder(new EmptyBorder(22, 22, 18, 22));

        JLabel t = new JLabel("Theme settings");
        t.setFont(f(21, Font.BOLD));
        t.setForeground(INK);
        JLabel sub = new JLabel("Glassy / glowing looks for every card. Current: " + THEME.name);
        sub.setFont(f(12, Font.PLAIN));
        sub.setForeground(MUTED);
        sub.setBorder(new EmptyBorder(6, 0, 16, 0));

        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        grid.setOpaque(false);
        for (Theme th : THEMES) grid.add(themeTile(th, dlg));

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        sp.getViewport().setBackground(CARD);
        sp.setPreferredSize(new Dimension(640, 430));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(14, 0, 0, 0));
        RoundButton close = outlineButton("Close");
        close.addActionListener(e -> dlg.dispose());
        actions.add(close);

        root.add(t);
        root.add(sub);
        root.add(sp);
        root.add(actions);

        dlg.setContentPane(root);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    JComponent themeTile(Theme th, JDialog dlg) {
        final boolean[] hover = {false};

        JPanel tile = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(th.card);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                if (th.glow) {
                    g2.setColor(new Color(th.glowColor.getRed(), th.glowColor.getGreen(),
                            th.glowColor.getBlue(), hover[0] ? 90 : 45));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                }
                g2.setColor(hover[0] ? th.primary : th.line);
                g2.setStroke(new BasicStroke(hover[0] ? 2f : 1f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        tile.setOpaque(false);
        tile.setBorder(new EmptyBorder(12, 12, 12, 12));
        tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel preview = new JPanel(new GridLayout(1, 3, 4, 0));
        preview.setOpaque(false);
        preview.setPreferredSize(new Dimension(100, 34));
        preview.add(swatch(th.nav, 8));
        preview.add(swatch(th.primary, 8));
        preview.add(swatch(th.glow ? th.glowColor : th.card2, 8));

        JLabel name = new JLabel(th.name);
        name.setFont(f(12, Font.BOLD));
        name.setForeground(th.ink);

        JLabel meta = new JLabel((th.dark ? "Dark" : "Light") + (th.glow ? " · glow" : " · flat"));
        meta.setFont(f(10, Font.PLAIN));
        meta.setForeground(th.muted);

        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new VStackLayout(2));
        textCol.add(name);
        textCol.add(meta);

        tile.add(preview, BorderLayout.NORTH);
        tile.add(textCol, BorderLayout.CENTER);

        tile.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover[0] = true; tile.repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover[0] = false; tile.repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                dlg.dispose();
                applyTheme(th);
                rebuildUi();
            }
        });
        return tile;
    }

    JComponent swatch(Color c, int radius) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    /* ================================================================
     *  DIALOG: RECORD (add / edit) — includes expiry reminder on save
     * ================================================================ */
    void openRecordDialog(String key, Row record) {
        Module m = modules.get(key);
        if (m == null) return;

        JDialog dlg = new JDialog(frame, true);
        dlg.setTitle(record == null ? "Add record" : "Edit record");

        JPanel root = new JPanel();
        root.setBackground(CARD);
        root.setLayout(new VStackLayout(0));
        root.setBorder(new EmptyBorder(24, 24, 20, 24));

        JLabel t = new JLabel((record == null ? "Add " : "Edit ") + m.title.toLowerCase(Locale.ENGLISH));
        t.setFont(f(21, Font.BOLD));
        t.setForeground(INK);

        JLabel sub = new JLabel("Preview data only. Saving here does not modify a database.");
        sub.setFont(f(12, Font.PLAIN));
        sub.setForeground(MUTED);
        sub.setBorder(new EmptyBorder(6, 0, 16, 0));

        JPanel form = new JPanel(new GridLayout(0, 2, 15, 14));
        form.setOpaque(false);

        LinkedHashMap<String, JComponent> inputs = new LinkedHashMap<>();
        for (Field fd : m.fields) {
            JComponent comp;
            if (fd.type.equals("select")) {
                JComboBox<String> box = new JComboBox<>(fd.options);
                box.setFont(f(13, Font.PLAIN));
                box.setBackground(CARD2);
                box.setForeground(INK);
                comp = box;
            } else {
                JTextField tf = new JTextField();
                tf.setFont(f(13, Font.PLAIN));
                tf.setBackground(CARD2);
                tf.setForeground(INK);
                tf.setCaretColor(INK);
                tf.setBorder(new CompoundBorder(new RoundBorder(9, CARD2, LINE), new EmptyBorder(6, 9, 6, 9)));
                comp = tf;
            }
            comp.setPreferredSize(new Dimension(200, 34));
            inputs.put(fd.name, comp);
            form.add(fieldBlock(fd.label, comp));

            if (record != null) {
                String val = record.s(fd.name);
                if (comp instanceof JComboBox) ((JComboBox<String>) comp).setSelectedItem(val);
                else if (comp instanceof JTextField) ((JTextField) comp).setText(val);
            }
        }
        if (m.fields.size() % 2 == 1) form.add(new JPanel());

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(CARD);
        formScroll.setPreferredSize(new Dimension(620,
                Math.min(520, Math.max(120, (m.fields.size() + 1) / 2 * 78 + 10))));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(16, 0, 0, 0));

        RoundButton cancel = outlineButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());

        RoundButton save = primaryButton("Save preview record");
        save.addActionListener(e -> {
            boolean isNew = (record == null);
            Row target = isNew ? new Row(System.currentTimeMillis()) : record;

            for (Field fd : m.fields) {
                JComponent comp = inputs.get(fd.name);
                if (comp instanceof JComboBox) {
                    target.set(fd.name, String.valueOf(((JComboBox<?>) comp).getSelectedItem()));
                } else {
                    String raw = ((JTextField) comp).getText().trim();
                    if (fd.type.equals("number")) {
                        try { target.set(fd.name, Double.parseDouble(raw)); }
                        catch (Exception ex) { target.set(fd.name, 0.0); }
                    } else {
                        target.set(fd.name, raw);
                    }
                }
            }
            if (isNew) data.get(key).add(0, target);

            dlg.dispose();
            render();

            // ---- expiry reminder (only for medicines / batches) ----
            String warn = expiryMessage(key, target);
            if (warn != null) {
                showExpiryAlert(warn);
                toast(warn.startsWith("⛔") ? "Expiry reminder: expired stock detected." : "Expiry reminder: near expiry.");
            } else if (key.equals("medicines") || key.equals("inventory")) {
                LocalDate d = parseDate(target.s("expiry"));
                if (d != null)
                    toast("Expiry looks good — " + ChronoUnit.DAYS.between(TODAY, d) + " days remaining.");
                else
                    toast("Saved to the in-memory preview data.");
            } else {
                toast("Saved to the in-memory preview data.");
            }
            updateBell();
        });

        actions.add(cancel);
        actions.add(save);

        root.add(t);
        root.add(sub);
        root.add(formScroll);
        root.add(actions);

        dlg.setContentPane(root);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    /* ================================================================
     *  DIALOG: CALENDAR EVENT
     * ================================================================ */
    void openEventDialog(CalEvent existing, LocalDate preset) {
        JDialog dlg = new JDialog(frame, true);
        dlg.setTitle(existing == null ? "New event" : "Edit event");

        JPanel root = new JPanel();
        root.setBackground(CARD);
        root.setLayout(new VStackLayout(0));
        root.setBorder(new EmptyBorder(24, 24, 20, 24));

        JLabel t = new JLabel(existing == null ? "New event" : "Edit event");
        t.setFont(f(21, Font.BOLD));
        t.setForeground(INK);
        JLabel sub = new JLabel("Events are stored in memory for this session only.");
        sub.setFont(f(12, Font.PLAIN));
        sub.setForeground(MUTED);
        sub.setBorder(new EmptyBorder(6, 0, 16, 0));

        JTextField titleF = textField(existing == null ? "" : existing.title);
        JTextField dateF = textField((existing == null ? preset : existing.date).toString());
        JTextField startF = textField(existing == null || existing.start == null
                ? "09:00" : existing.start.toString());
        JTextField endF = textField(existing == null || existing.end == null
                ? "10:00" : existing.end.toString());
        JTextField locF = textField(existing == null ? "" : existing.location);
        JTextField notesF = textField(existing == null ? "" : existing.notes);

        JCheckBox allDay = new JCheckBox("All day");
        allDay.setOpaque(false);
        allDay.setFont(f(12, Font.PLAIN));
        allDay.setForeground(INK);
        allDay.setSelected(existing != null && existing.allDay);

        String[] names = new String[CALENDARS.length];
        for (int i = 0; i < CALENDARS.length; i++) names[i] = CALENDARS[i][0];
        JComboBox<String> calBox = new JComboBox<>(names);
        calBox.setFont(f(13, Font.PLAIN));
        calBox.setBackground(CARD2);
        calBox.setForeground(INK);
        if (existing != null) calBox.setSelectedItem(existing.cal);

        JPanel row1 = new JPanel(new GridLayout(1, 2, 15, 0));
        row1.setOpaque(false);
        row1.add(fieldBlock("Date (YYYY-MM-DD)", dateF));
        row1.add(fieldBlock("Calendar", calBox));

        JPanel row2 = new JPanel(new GridLayout(1, 3, 15, 0));
        row2.setOpaque(false);
        row2.add(fieldBlock("Start (HH:mm)", startF));
        row2.add(fieldBlock("End (HH:mm)", endF));
        row2.add(fieldBlock(" ", allDay));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new VStackLayout(14));
        form.add(fieldBlock("Title", titleF));
        form.add(row1);
        form.add(row2);
        form.add(fieldBlock("Location", locF));
        form.add(fieldBlock("Notes", notesF));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(18, 0, 0, 0));

        if (existing != null) {
            RoundButton del = new RoundButton("Delete", tintBg(RED), mix(RED, CARD, 0.75),
                    RED, mix(RED, CARD, 0.6), 9);
            del.addActionListener(e -> {
                events.remove(existing);
                dlg.dispose();
                render();
                toast("Event deleted.");
            });
            actions.add(del);
        }

        RoundButton cancel = outlineButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());

        RoundButton save = primaryButton(existing == null ? "Create event" : "Save event");
        save.addActionListener(e -> {
            LocalDate d;
            try { d = LocalDate.parse(dateF.getText().trim()); }
            catch (Exception ex) { toast("Date must be YYYY-MM-DD."); return; }

            LocalTime s = null, en = null;
            if (!allDay.isSelected()) {
                try {
                    s = LocalTime.parse(startF.getText().trim());
                    en = LocalTime.parse(endF.getText().trim());
                } catch (Exception ex) { toast("Times must be HH:mm."); return; }
                if (en.isBefore(s)) { toast("End time is before start time."); return; }
            }
            if (titleF.getText().trim().isEmpty()) { toast("Please give the event a title."); return; }

            CalEvent ev = existing != null ? existing : new CalEvent();
            if (existing == null) ev.id = System.nanoTime();
            ev.title = titleF.getText().trim();
            ev.date = d;
            ev.allDay = allDay.isSelected();
            ev.start = s;
            ev.end = en;
            ev.cal = String.valueOf(calBox.getSelectedItem());
            ev.location = locF.getText().trim();
            ev.notes = notesF.getText().trim();
            if (existing == null) events.add(ev);

            calSelected = d;
            calMonth = d.withDayOfMonth(1);
            dlg.dispose();
            render();
            toast(existing == null ? "Event created." : "Event updated.");
        });

        actions.add(cancel);
        actions.add(save);

        root.add(t);
        root.add(sub);
        root.add(form);
        root.add(actions);

        dlg.setContentPane(root);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    /* ================================================================
     *  TABLE PLUMBING
     * ================================================================ */
    static final Set<String> MONEY_FIELDS = Set.of("total", "subtotal", "discount", "tax", "cost", "price");

    static class RowTableModel extends AbstractTableModel {
        final String moduleKey;
        final List<String[]> cols;
        final List<Row> rows;
        final boolean actions;

        RowTableModel(String moduleKey, List<String[]> cols, List<Row> rows, boolean actions) {
            this.moduleKey = moduleKey;
            this.cols = cols;
            this.rows = rows;
            this.actions = actions;
        }

        public int getRowCount() { return rows.size(); }
        public int getColumnCount() { return cols.size() + (actions ? 1 : 0); }
        public boolean isCellEditable(int r, int c) { return false; }
        public Row rowAt(int modelRow) { return rows.get(modelRow); }
        public String fieldAt(int col) { return col < cols.size() ? cols.get(col)[0] : null; }

        public boolean isBadgeCol(int col) {
            String fld = fieldAt(col);
            return "status".equals(fld) || "active".equals(fld);
        }

        public String getColumnName(int col) {
            if (actions && col == cols.size()) return "Actions";
            return cols.get(col)[1];
        }

        public Object getValueAt(int r, int col) {
            Row row = rows.get(r);
            if (actions && col == cols.size()) return isArchived(row) ? "Restore" : "Archive";
            String fld = cols.get(col)[0];
            Object raw = row.get(fld);
            if (MONEY_FIELDS.contains(fld)) return money(raw);
            if (raw == null) return "—";
            if (raw instanceof Double) {
                double d = (Double) raw;
                if (d == Math.floor(d)) return String.valueOf((long) d);
            }
            return String.valueOf(raw);
        }
    }

    static boolean isArchived(Row r) {
        String st = r.s("status");
        String ac = r.s("active");
        return st.equals("Disabled") || st.equals("Archived") || st.equals("Cancelled")
                || st.equals("Rejected") || ac.equals("Archived");
    }

    static class HeaderRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc,
                                                       int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            l.setOpaque(true);
            l.setBackground(CARD2);
            l.setForeground(MUTED);
            l.setFont(f(10, Font.BOLD));
            l.setBorder(new EmptyBorder(0, 13, 0, 13));
            l.setText(v == null ? "" : String.valueOf(v).toUpperCase(Locale.ENGLISH));
            return l;
        }
    }

    static class CellRenderer extends DefaultTableCellRenderer {
        final boolean strong;
        CellRenderer(boolean strong) { this.strong = strong; }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc,
                                                       int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            l.setOpaque(true);
            l.setBackground(sel ? ROW_SEL : CARD);
            l.setForeground(strong ? INK : mix(INK, MUTED, 0.5));
            l.setFont(f(13, strong ? Font.BOLD : Font.PLAIN));
            l.setBorder(new EmptyBorder(0, 13, 0, 13));
            return l;
        }
    }

    static class BadgeRenderer implements TableCellRenderer {
        final JPanel wrap = new JPanel(new GridBagLayout());
        final Pill pill = new Pill();
        BadgeRenderer() {
            wrap.setOpaque(true);
            GridBagConstraints gc = new GridBagConstraints();
            gc.anchor = GridBagConstraints.WEST;
            gc.insets = new Insets(0, 13, 0, 0);
            wrap.add(pill, gc);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc,
                                                       int r, int c) {
            wrap.setBackground(sel ? ROW_SEL : CARD);
            String s = v == null ? "—" : String.valueOf(v);
            pill.setText(s);
            Color[] st = badgeStyle(s);
            pill.setPill(st[0], st[1]);
            return wrap;
        }
    }

    static class ActionsRenderer extends JComponent implements TableCellRenderer {
        static final int X1 = 13, W1 = 52, GAP = 8, W2 = 72, H = 26;
        boolean selected;
        String secondLabel = "Archive";

        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc,
                                                       int r, int c) {
            selected = sel;
            secondLabel = v == null ? "Archive" : String.valueOf(v);
            return this;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(selected ? ROW_SEL : CARD);
            g2.fillRect(0, 0, getWidth(), getHeight());
            int y = (getHeight() - H) / 2;
            drawButton(g2, "Edit", X1, y, W1, H);
            drawButton(g2, secondLabel, X1 + W1 + GAP, y, W2, H);
            g2.dispose();
        }

        void drawButton(Graphics2D g2, String text, int x, int y, int w, int h) {
            g2.setColor(CARD2);
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(LINE);
            g2.drawRoundRect(x, y, w, h, 10, 10);
            g2.setColor(mix(INK, MUTED, 0.35));
            g2.setFont(f(11, Font.BOLD));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (w - fm.stringWidth(text)) / 2;
            int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
        }
    }

    JScrollPane tableScroll(String moduleKey, List<String[]> cols, List<Row> rows, boolean actions) {
        RowTableModel model = new RowTableModel(moduleKey, cols, rows, actions);
        JTable table = new JTable(model);

        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(LINE);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setBackground(CARD);
        table.setForeground(INK);
        table.setFont(f(13, Font.PLAIN));
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(INK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        int actionsIndex = actions ? model.getColumnCount() - 1 : -1;
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(i == actionsIndex ? 150 : 120);
            if (i == actionsIndex) {
                col.setCellRenderer(new ActionsRenderer());
                col.setMinWidth(150);
                col.setMaxWidth(170);
            } else if (model.isBadgeCol(i)) {
                col.setCellRenderer(new BadgeRenderer());
            } else {
                col.setCellRenderer(new CellRenderer(i == 0));
            }
        }

        if (actions) {
            table.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int viewRow = table.rowAtPoint(e.getPoint());
                    int viewCol = table.columnAtPoint(e.getPoint());
                    if (viewRow < 0 || viewCol != actionsIndex) return;
                    Rectangle cell = table.getCellRect(viewRow, viewCol, true);
                    int x = e.getX() - cell.x;
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    Row record = model.rowAt(modelRow);
                    if (x >= ActionsRenderer.X1 && x <= ActionsRenderer.X1 + ActionsRenderer.W1) {
                        openRecordDialog(moduleKey, record);
                    } else if (x >= ActionsRenderer.X1 + ActionsRenderer.W1 + ActionsRenderer.GAP
                            && x <= ActionsRenderer.X1 + ActionsRenderer.W1 + ActionsRenderer.GAP
                                    + ActionsRenderer.W2) {
                        archiveRecord(moduleKey, record);
                    }
                }
            });
        }

        JScrollPane sp;
        if (rows.isEmpty()) {
            JLabel empty = new JLabel("No records to display.", SwingConstants.CENTER);
            empty.setForeground(MUTED);
            empty.setFont(f(13, Font.PLAIN));
            empty.setOpaque(true);
            empty.setBackground(CARD);
            sp = new JScrollPane(empty);
            sp.setPreferredSize(new Dimension(140, 120));
        } else {
            sp = new JScrollPane(table);
            int h = Math.min(rows.size(), 12) * 44 + 44;
            sp.setPreferredSize(new Dimension(140, h));
        }
        sp.setBorder(null);
        sp.setBackground(CARD);
        sp.getViewport().setBackground(CARD);
        return sp;
    }

    void archiveRecord(String moduleKey, Row record) {
        switch (moduleKey) {
            case "suppliers":
            case "customers":
                record.set("active", "Archived".equals(record.s("active")) ? "Active" : "Archived");
                break;
            case "users":
                record.set("status", "Disabled".equals(record.s("status")) ? "Active" : "Disabled");
                break;
            case "sales":
                record.set("status", "Cancelled".equals(record.s("status")) ? "Confirmed" : "Cancelled");
                break;
            case "purchases":
                record.set("status", "Cancelled".equals(record.s("status")) ? "Confirmed" : "Cancelled");
                break;
            case "prescriptions":
                record.set("status", "Rejected".equals(record.s("status")) ? "Pending" : "Rejected");
                break;
            default:
                toast("Use the Java server application for validated stock transactions.");
                return;
        }
        render();
        toast("Preview record updated.");
    }

    /* ================================================================
     *  UI HELPERS
     * ================================================================ */
    JPanel cardPanel() {
        GlassPanel p = new GlassPanel(new BorderLayout(0, 14));
        p.setBorder(new EmptyBorder(16, 18, 16, 18));
        return p;
    }

    JPanel grid(int rows, int cols, int hgap, int vgap) {
        JPanel p = new JPanel(new GridLayout(rows, cols, hgap, vgap));
        p.setOpaque(false);
        return p;
    }

    JPanel split(JComponent left, JComponent right, double lw, double rw) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        gc.gridy = 0;

        gc.gridx = 0; gc.weightx = lw; gc.insets = new Insets(0, 0, 0, 16);
        p.add(left, gc);
        gc.gridx = 1; gc.weightx = rw; gc.insets = new Insets(0, 0, 0, 0);
        p.add(right, gc);
        return p;
    }

    JPanel sectionHead(String titleText, JComponent right) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(titleText);
        l.setFont(f(16, Font.BOLD));
        l.setForeground(INK);
        p.add(l, BorderLayout.WEST);
        if (right != null) p.add(right, BorderLayout.EAST);
        return p;
    }

    JPanel statCard(String label, String value, String hint, String icon, Color accent) {
        GlassPanel card = new GlassPanel(null);
        card.setLayout(new VStackLayout(4));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.accent = accent;

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(f(12, Font.PLAIN));
        l.setForeground(MUTED);
        top.add(l, BorderLayout.WEST);

        JLabel ic = new JLabel(icon, SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(mix(accent, CARD, THEME.dark ? 0.72 : 0.86));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        ic.setPreferredSize(new Dimension(33, 33));
        ic.setForeground(accent);
        ic.setFont(f(12, Font.BOLD));
        top.add(ic, BorderLayout.EAST);

        JLabel v = new JLabel(value);
        v.setFont(f(29, Font.BOLD));
        v.setForeground(INK);

        JLabel h = new JLabel(hint);
        h.setFont(f(11, Font.PLAIN));
        h.setForeground(MUTED);

        card.add(top);
        card.add(v);
        card.add(h);
        return card;
    }

    JPanel quickAction(String title, String sub, String navTo) {
        RoundButton b = new RoundButton("<html><b>" + title + "</b><br>"
                + "<font size='2' color='" + hex(MUTED) + "'>" + sub + "</font></html>",
                CARD, CARD2, INK, LINE, 11);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(f(12, Font.BOLD));
        b.setBorder(new EmptyBorder(12, 13, 12, 13));
        b.addActionListener(e -> {
            if ("__theme".equals(navTo)) { openThemeDialog(); return; }
            current = navTo;
            render();
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(b, BorderLayout.CENTER);
        return wrap;
    }

    JPanel fieldBlock(String label, JComponent comp) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new VStackLayout(6));
        if (label != null && !label.trim().isEmpty()) {
            JLabel l = new JLabel(label);
            l.setFont(f(12, Font.BOLD));
            l.setForeground(INK);
            p.add(l);
        }
        p.add(comp);
        return p;
    }

    JTextField textField(String value) {
        JTextField tf = new JTextField(value);
        tf.setFont(f(13, Font.PLAIN));
        tf.setPreferredSize(new Dimension(200, 34));
        tf.setBackground(CARD2);
        tf.setForeground(INK);
        tf.setCaretColor(INK);
        tf.setBorder(new CompoundBorder(new RoundBorder(9, CARD2, LINE), new EmptyBorder(6, 9, 6, 9)));
        return tf;
    }

    RoundButton primaryButton(String text) {
        return new RoundButton(text, BLUE, BLUE_DARK, Color.WHITE, null, 9);
    }

    RoundButton outlineButton(String text) {
        return new RoundButton(text, CARD, CARD2, mix(INK, MUTED, 0.4), LINE, 9);
    }

    void toast(String message) {
        JWindow w = new JWindow(frame);
        JLabel l = new JLabel(message);
        l.setOpaque(true);
        l.setBackground(NAV);
        l.setForeground(Color.WHITE);
        l.setFont(f(12, Font.PLAIN));
        l.setBorder(new EmptyBorder(12, 16, 12, 16));
        w.getContentPane().add(l);
        w.pack();
        Point p = frame.getLocationOnScreen();
        w.setLocation(p.x + frame.getWidth() - w.getWidth() - 30,
                p.y + frame.getHeight() - w.getHeight() - 60);
        w.setVisible(true);
        javax.swing.Timer timer = new javax.swing.Timer(2600, e -> w.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    /* ================================================================
     *  FORMATTING HELPERS
     * ================================================================ */
    static String money(Object v) {
        double n = 0;
        if (v instanceof Number) n = ((Number) v).doubleValue();
        else {
            try { n = Double.parseDouble(String.valueOf(v)); } catch (Exception ignored) { }
        }
        return "₹" + inr(n);
    }

    static String inr(double n) {
        boolean neg = n < 0;
        n = Math.abs(n);
        String s = String.format(Locale.US, "%.2f", n);
        String[] parts = s.split("\\.");
        String intPart = parts[0];
        String last3 = intPart.length() > 3 ? intPart.substring(intPart.length() - 3) : intPart;
        String rest = intPart.length() > 3 ? intPart.substring(0, intPart.length() - 3) : "";
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = rest.length() - 1; i >= 0; i--) {
            sb.insert(0, rest.charAt(i));
            count++;
            if (count % 2 == 0 && i > 0) sb.insert(0, ',');
        }
        String out = (sb.length() > 0 ? sb + "," : "") + last3 + "." + parts[1];
        return (neg ? "-" : "") + out;
    }

    static Color[] badgeStyle(String s) {
        String k = s == null ? "" : s.toLowerCase(Locale.ENGLISH);
        Color fg, bg;
        if (k.equals("active") || k.equals("confirmed") || k.equals("received") || k.equals("verified")) {
            fg = new Color(0x14735f); bg = new Color(0xe6f6f0);
        } else if (k.equals("low") || k.equals("low stock") || k.equals("pending")) {
            fg = new Color(0x946208); bg = new Color(0xfff3d9);
        } else if (k.contains("expir") || k.equals("cancelled") || k.equals("rejected")
                || k.equals("disabled") || k.equals("archived")) {
            fg = new Color(0xb12d3a); bg = new Color(0xffeaec);
        } else if (k.equals("draft")) {
            fg = new Color(0x405ed0); bg = new Color(0xedf1ff);
        } else {
            fg = new Color(0x687386); bg = new Color(0xf0f2f6);
        }
        if (THEME.dark) bg = mix(fg, CARD, 0.78);
        return new Color[]{bg, fg};
    }

    static Color mix(Color a, Color b, double t) {
        t = Math.max(0, Math.min(1, t));
        return new Color(
                (int) (a.getRed() * (1 - t) + b.getRed() * t),
                (int) (a.getGreen() * (1 - t) + b.getGreen() * t),
                (int) (a.getBlue() * (1 - t) + b.getBlue() * t));
    }

    static Color tintBg(Color accent) { return mix(accent, CARD, THEME.dark ? 0.80 : 0.90); }

    static Color tintText(Color accent) {
        return THEME.dark ? mix(accent, Color.WHITE, 0.45) : mix(accent, Color.BLACK, 0.22);
    }

    static String hex(Color c) { return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()); }

    static LocalDate parseDate(String s) {
        try { return LocalDate.parse(s); } catch (Exception e) { return null; }
    }

    static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}