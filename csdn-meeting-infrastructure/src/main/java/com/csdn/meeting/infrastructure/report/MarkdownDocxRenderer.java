package com.csdn.meeting.infrastructure.report;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.Parser;

/**
 * 将 CommonMark Markdown 渲染为 Word（docx）段落结构，使用中文字体名「Noto Sans SC」。
 */
final class MarkdownDocxRenderer extends AbstractVisitor {

    private static final String FONT = "Noto Sans SC";

    private final XWPFDocument document;
    private int orderedIndex = 1;
    /** 非空时表示当前处于标题内，需设置字号 */
    private Integer headingFontSizePoints;

    MarkdownDocxRenderer(XWPFDocument document) {
        this.document = document;
    }

    void render(String markdown) {
        Node root = Parser.builder().build().parse(markdown == null ? "" : markdown);
        root.accept(this);
    }

    @Override
    public void visit(Document document) {
        visitChildren(document);
    }

    @Override
    public void visit(Heading heading) {
        XWPFParagraph p = document.createParagraph();
        p.setSpacingBefore(heading.getLevel() == 1 ? 280 : 200);
        int size = heading.getLevel() == 1 ? 28 : (heading.getLevel() == 2 ? 22 : 18);
        headingFontSizePoints = size;
        try {
            appendInlines(p, heading, true, false);
        } finally {
            headingFontSizePoints = null;
        }
    }

    @Override
    public void visit(Paragraph paragraph) {
        if (paragraph.getParent() instanceof ListItem) {
            return;
        }
        XWPFParagraph p = document.createParagraph();
        appendInlines(p, paragraph, false, false);
    }

    @Override
    public void visit(BulletList bulletList) {
        visitChildren(bulletList);
    }

    @Override
    public void visit(OrderedList orderedList) {
        orderedIndex = 1;
        visitChildren(orderedList);
    }

    @Override
    public void visit(ListItem listItem) {
        XWPFParagraph p = document.createParagraph();
        p.setIndentationLeft(400);
        XWPFRun prefix = p.createRun();
        if (listItem.getParent() instanceof OrderedList) {
            prefix.setText(orderedIndex++ + ". ");
        } else {
            prefix.setText("• ");
        }
        applyFont(prefix);

        Node child = listItem.getFirstChild();
        if (child instanceof Paragraph) {
            appendInlines(p, (Paragraph) child, false, false);
        } else if (child != null) {
            child.accept(this);
        }
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        XWPFParagraph p = document.createParagraph();
        XWPFRun r = p.createRun();
        r.setText("—————————————");
        applyFont(r);
    }

    private void appendInlines(XWPFParagraph p, Node container, boolean bold, boolean italic) {
        for (Node c = container.getFirstChild(); c != null; c = c.getNext()) {
            appendInlineNode(p, c, bold, italic);
        }
    }

    private void appendInlineNode(XWPFParagraph p, Node n, boolean bold, boolean italic) {
        if (n instanceof Text) {
            XWPFRun r = p.createRun();
            r.setBold(bold);
            r.setItalic(italic);
            if (headingFontSizePoints != null) {
                r.setFontSize(headingFontSizePoints);
            }
            r.setText(((Text) n).getLiteral());
            applyFont(r);
            return;
        }
        if (n instanceof SoftLineBreak || n instanceof HardLineBreak) {
            p.createRun().addBreak();
            return;
        }
        if (n instanceof StrongEmphasis) {
            for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
                appendInlineNode(p, c, true, italic);
            }
            return;
        }
        if (n instanceof Emphasis) {
            for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
                appendInlineNode(p, c, bold, true);
            }
            return;
        }
        if (n instanceof Code) {
            XWPFRun r = p.createRun();
            r.setText(((Code) n).getLiteral());
            r.setFontFamily("Courier New");
            return;
        }
        appendInlines(p, n, bold, italic);
    }

    private static void applyFont(XWPFRun r) {
        r.setFontFamily(FONT);
    }
}
