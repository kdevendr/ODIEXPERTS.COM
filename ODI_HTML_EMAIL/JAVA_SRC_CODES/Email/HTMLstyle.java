package Email;

import java.sql.*;

public class HTMLstyle
{

    public static StringBuilder style1(Connection conn, String HTMLsql)
        throws SQLException
    {
        StringBuilder build = new StringBuilder();
        build.append("<style type=\"text/css\">#hor-minimalist-b { \tfont-family: \"Lucida Sans Unicod" +
"e\", \"Lucida Grande\", Sans-Serif; \tfont-size: 12px; \tbackground: #fff; \tmar" +
"gin: 45px; \t\tborder-collapse: collapse; \ttext-align: left; }#hor-minimalist-b" +
" th { \tfont-size: 14px; \tfont-weight: normal; \tcolor: #039; \tpadding: 10px 8" +
"px; \tborder-bottom: 2px solid #6678b1; }#hor-minimalist-b td { \tborder-bottom:" +
" 1px solid #ccc; \tcolor: #669; \tpadding: 6px 8px; }#hor-minimalist-b tbody tr:" +
"hover td { \tcolor: #009; }</style>"
);
        build.append("<table id=\"hor-minimalist-b\"");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(HTMLsql);
        ResultSetMetaData md = rs.getMetaData();
        int colno = md.getColumnCount();
        build.append("<thead><tr>");
        for(int i = 0; i < colno; i++)
        {
            build.append((new StringBuilder("<th scope=\"col\">")).append(md.getColumnName(i + 1)).append("</th>").toString());
        }

        build.append("</tr></thead>");
        build.append("<tbody>");
        for(; rs.next(); build.append("</tr>"))
        {
            build.append("<tr>");
            for(int i = 0; i < colno; i++)
            {
                build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
            }

        }

        build.append("</tbody>");
        build.append("</table>");
        rs.close();
        st.close();
        return build;
    }

    public static StringBuilder style2(Connection conn, String HTMLsql)
        throws SQLException
    {
        StringBuilder build = new StringBuilder();
        build.append("<style type=\"text/css\">#box-table-a { \tfont-family: \"Lucida Sans Unicode\", " +
"\"Lucida Grande\", Sans-Serif; \tfont-size: 12px; \tmargin: 45px; \t\ttext-align" +
": left; \tborder-collapse: collapse; }#box-table-a th { \tfont-size: 13px; \tfon" +
"t-weight: normal; \tpadding: 8px; \tbackground: #b9c9fe; \tborder-top: 4px solid" +
" #aabcfe; \tborder-bottom: 1px solid #fff; \tcolor: #039; }#box-table-a td { \tp" +
"adding: 8px; \tbackground: #e8edff; \tborder-bottom: 1px solid #fff; \tcolor: #6" +
"69; \tborder-top: 1px solid transparent; }#box-table-a tr:hover td { \tbackgroun" +
"d: #d0dafd; \tcolor: #339; }</style>"
);
        build.append("<table id=\"box-table-a\"");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(HTMLsql);
        ResultSetMetaData md = rs.getMetaData();
        int colno = md.getColumnCount();
        build.append("<thead><tr>");
        for(int i = 0; i < colno; i++)
        {
            build.append((new StringBuilder("<th scope=\"col\">")).append(md.getColumnName(i + 1)).append("</th>").toString());
        }

        build.append("</tr></thead>");
        build.append("<tbody>");
        for(; rs.next(); build.append("</tr>"))
        {
            build.append("<tr>");
            for(int i = 0; i < colno; i++)
            {
                build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
            }

        }

        build.append("</tbody>");
        build.append("</table>");
        rs.close();
        st.close();
        return build;
    }

    public static StringBuilder style3(Connection conn, String HTMLsql)
        throws SQLException
    {
        StringBuilder build = new StringBuilder();
        build.append("<style type=\"text/css\">#hor-zebra { \tfont-family: \"Lucida Sans Unicode\", \"" +
"Lucida Grande\", Sans-Serif; \tfont-size: 12px; \tmargin: 45px; \t\ttext-align: " +
"left; \tborder-collapse: collapse; }#hor-zebra th { \tfont-size: 14px; \tfont-we" +
"ight: normal; \tpadding: 10px 8px; \tcolor: #039; }#hor-zebra td { \tpadding: 8p" +
"x; \tcolor: #669; }#hor-zebra .odd { \tbackground: #e8edff; }</style>"
);
        build.append("<table id=\"hor-zebra\"");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(HTMLsql);
        ResultSetMetaData md = rs.getMetaData();
        int colno = md.getColumnCount();
        build.append("<thead><tr>");
        for(int i = 0; i < colno; i++)
        {
            build.append((new StringBuilder("<th scope=\"col\">")).append(md.getColumnName(i + 1)).append("</th>").toString());
        }

        build.append("</tr></thead>");
        build.append("<tbody>");
        int zz = 0;
        while(rs.next()) 
        {
            if(++zz % 2 == 0)
            {
                build.append("<tr class=\"odd\">");
                for(int i = 0; i < colno; i++)
                {
                    build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
                }

                build.append("</tr>");
            } else
            {
                build.append("<tr>");
                for(int i = 0; i < colno; i++)
                {
                    build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
                }

                build.append("</tr>");
            }
        }
        build.append("</tbody>");
        build.append("</table>");
        rs.close();
        st.close();
        return build;
    }

    public static StringBuilder style4(Connection conn, String HTMLsql)
        throws SQLException
    {
        StringBuilder build = new StringBuilder();
        build.append("<style type=\"text/css\">#ver-zebra { \tfont-family: \"Lucida Sans Unicode\", \"" +
"Lucida Grande\", Sans-Serif; \tfont-size: 12px; \tmargin: 45px; \t\ttext-align: " +
"left; \tborder-collapse: collapse; }#ver-zebra th { \tfont-size: 14px; \tfont-we" +
"ight: normal; \tpadding: 12px 15px; \tborder-right: 1px solid #fff; \tborder-lef" +
"t: 1px solid #fff; \tcolor: #039; }#ver-zebra td { \tpadding: 8px 15px; \tborder" +
"-right: 1px solid #fff; \tborder-left: 1px solid #fff; \tcolor: #669; }.vzebra-o" +
"dd { \tbackground: #eff2ff; }.vzebra-even { \tbackground: #e8edff; }#ver-zebra #" +
"vzebra-adventure, #ver-zebra #vzebra-children { \tbackground: #d0dafd; \tborder-" +
"bottom: 1px solid #c8d4fd; }#ver-zebra #vzebra-comedy, #ver-zebra #vzebra-action" +
" { \tbackground: #dce4ff; \tborder-bottom: 1px solid #d6dfff; }</style>"
);
        build.append("<table id=\"ver-zebra\"");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(HTMLsql);
        ResultSetMetaData md = rs.getMetaData();
        int colno = md.getColumnCount();
        for(int i = 0; i < colno; i++)
        {
            if((i + 1) % 2 == 1)
            {
                build.append("<col class=\"vzebra-even\" />");
            } else
            {
                build.append("<col class=\"vzebra-odd\" />");
            }
        }

        build.append("<thead><tr>");
        for(int i = 0; i < colno; i++)
        {
            build.append((new StringBuilder("<th scope=\"col\">")).append(md.getColumnName(i + 1)).append("</th>").toString());
        }

        build.append("</tr></thead>");
        build.append("<tbody>");
        int zz = 0;
        while(rs.next()) 
        {
            if(++zz % 2 == 0)
            {
                build.append("<tr class=\"odd\">");
                for(int i = 0; i < colno; i++)
                {
                    build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
                }

                build.append("</tr>");
            } else
            {
                build.append("<tr>");
                for(int i = 0; i < colno; i++)
                {
                    build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
                }

                build.append("</tr>");
            }
        }
        build.append("</tbody>");
        build.append("</table>");
        rs.close();
        st.close();
        return build;
    }

    public static StringBuilder style5(Connection conn, String HTMLsql)
        throws SQLException
    {
        StringBuilder build = new StringBuilder();
        build.append("<style type=\"text/css\">#one-column-emphasis { \tfont-family: \"Lucida Sans Uni" +
"code\", \"Lucida Grande\", Sans-Serif; \tfont-size: 12px; \tmargin: 45px; \t\tte" +
"xt-align: left; \tborder-collapse: collapse; }#one-column-emphasis th { \tfont-s" +
"ize: 14px; \tfont-weight: normal; \tpadding: 12px 15px; \tcolor: #039; }#one-col" +
"umn-emphasis td { \tpadding: 10px 15px; \tcolor: #669; \tborder-top: 1px solid #" +
"e8edff; }.oce-first { \tbackground: #d0dafd; \tborder-right: 10px solid transpar" +
"ent; \tborder-left: 10px solid transparent; }#one-column-emphasis tr:hover td { " +
"\tcolor: #339; \tbackground: #eff2ff; }</style>"
);
        build.append("<table id=\"one-column-emphasis\"");
        build.append("<colgroup><col class=\"oce-first\" /> </colgroup>");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(HTMLsql);
        ResultSetMetaData md = rs.getMetaData();
        int colno = md.getColumnCount();
        build.append("<thead><tr>");
        for(int i = 0; i < colno; i++)
        {
            build.append((new StringBuilder("<th scope=\"col\">")).append(md.getColumnName(i + 1)).append("</th>").toString());
        }

        build.append("</tr></thead>");
        build.append("<tbody>");
        for(; rs.next(); build.append("</tr>"))
        {
            build.append("<tr>");
            for(int i = 0; i < colno; i++)
            {
                build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
            }

        }

        build.append("</tbody>");
        build.append("</table>");
        rs.close();
        st.close();
        return build;
    }

    public static StringBuilder style6(Connection conn, String HTMLsql)
        throws SQLException
    {
        StringBuilder build = new StringBuilder();
        build.append("<style type=\"text/css\">#newspaper-a { \tfont-family: \"Lucida Sans Unicode\", " +
"\"Lucida Grande\", Sans-Serif; \tfont-size: 12px; \tmargin: 45px; \ttext-align: " +
"left; \tborder-collapse: collapse; \tborder: 1px solid #69c; }#newspaper-a th { " +
"\tpadding: 12px 17px 12px 17px; \tfont-weight: normal; \tfont-size: 14px; \tcolo" +
"r: #039; \tborder-bottom: 1px dashed #69c; }#newspaper-a td { \tpadding: 7px 17p" +
"x 7px 17px; \tcolor: #669; }#newspaper-a tbody tr:hover td { \tcolor: #339; \tba" +
"ckground: #d0dafd; }</style>"
);
        build.append("<table id=\"newspaper-a\"");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(HTMLsql);
        ResultSetMetaData md = rs.getMetaData();
        int colno = md.getColumnCount();
        int len[] = new int[colno];
        for(int i = 0; i < colno; i++)
        {
            len[i] = 0;
        }

        build.append("<thead><tr>");
        for(int i = 0; i < colno; i++)
        {
            build.append((new StringBuilder("<th scope=\"col\">")).append(md.getColumnName(i + 1)).append("</th>").toString());
        }

        build.append("</tr></thead>");
        build.append("<tbody>");
        for(; rs.next(); build.append("</tr>"))
        {
            build.append("<tr>");
            for(int i = 0; i < colno; i++)
            {
                build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
            }

        }

        build.append("</tbody>");
        build.append("</table>");
        rs.close();
        st.close();
        return build;
    }

    public static StringBuilder Defaultstyle(Connection conn, String HTMLsql)
        throws SQLException
    {
        StringBuilder build = new StringBuilder();
        build.append("<table>");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(HTMLsql);
        ResultSetMetaData md = rs.getMetaData();
        int colno = md.getColumnCount();
        build.append("<thead><tr>");
        for(int i = 0; i < colno; i++)
        {
            build.append((new StringBuilder("<th>")).append(md.getColumnName(i + 1)).append("</th>").toString());
        }

        build.append("</tr></thead>");
        build.append("<tbody>");
        for(; rs.next(); build.append("</tr>"))
        {
            build.append("<tr>");
            for(int i = 0; i < colno; i++)
            {
                build.append((new StringBuilder("<td>")).append(rs.getObject(i + 1)).append("</td>").toString());
            }

        }

        build.append("</tbody>");
        build.append("</table>");
        rs.close();
        st.close();
        return build;
    }
}
