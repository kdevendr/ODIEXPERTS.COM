package FileLoad;

import api.getInfo;
import io.FileUtils;
import io.LineIterator;
import io.input.ReversedLinesFileReader;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.Date;

// Referenced classes of package FileLoad:
//            getStatus, HandleData, DebugFileErrors, FileMove

public class DebugFile
{

    private static LineIterator it;
    private static int RowNo;
    private static int LineNo;
    private static int ProcLine;
    private static int escapeLine;
    private static int commitNo;
    private static int keyNo;
    private static int ProcessingRowNo = 0;
    private static ReversedLinesFileReader revFL;
    private static ArrayList footerData = new ArrayList();
    private static String pos[];
    private static String Datacol[];
    private static String col[];
    private static String tempcol[];
    private static PreparedStatement ps;
    private static PreparedStatement pserror;
    private static StringBuilder build = new StringBuilder();
    private static String fileName;
    private static String Line;
    private static String data;
    private static String tempcolumn;
    private static String DataColumns = "";
    private static HashMap map = new HashMap();
    private static HashMap srccolmap = new HashMap();

    public DebugFile()
    {
    }

    public static void ProcessFile(Connection conn, String Dir, String Mask, String colpos, String Move_Copy_Files, String Move_Copy_Files_Dir, String selectiveLine, int Heading, 
            int Footer, String delimiter, int commitsize, String trgtquery, String ErrorSQL, int sessNo, String tableName, 
            String tablealias, String columns, String textDelim, String CharacterSet, String ignoreLine, String Remove_Special_Characters, String srccol)
        throws SQLException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, InterruptedException
    {
        getStatus.DebugFileInitialize(sessNo);
        conn.setAutoCommit(false);
        col = columns.split(",\t");
        String sourcecol[] = srccol.split(";");
        String as[];
        int l = (as = sourcecol).length;
        for(int k = 0; k < l; k++)
        {
            String string = as[k];
            String eachcol[] = string.split(",");
            srccolmap.put(Integer.valueOf(Integer.parseInt(eachcol[1].toString())), eachcol[0].toString());
        }

        tempcolumn = columns;
        DataColumns = columns;
        for(int j = 0; j < srccolmap.size(); j++)
        {
            tempcolumn = tempcolumn.replace((new StringBuilder(String.valueOf(tablealias))).append(srccolmap.get(Integer.valueOf(j + 1)).toString()).toString(), "?");
            DataColumns = DataColumns.replace((new StringBuilder(String.valueOf(tablealias))).append(srccolmap.get(Integer.valueOf(j + 1)).toString()).toString(), (new StringBuilder("Datacol[")).append(j).append("]").toString());
        }

        tempcolumn = tempcolumn.replace("\t", "");
        DataColumns = DataColumns.replace("\t", "");
        trgtquery = (new StringBuilder(String.valueOf(trgtquery))).append(" VALUES (").append(tempcolumn).append(" )").toString();
        ErrorSQL = (new StringBuilder(String.valueOf(ErrorSQL))).append(" VALUES (?,?,?,?,?,?,").append(tempcolumn).append(" )").toString();
        ps = conn.prepareStatement(trgtquery);
        pserror = conn.prepareStatement(ErrorSQL);
        File files[] = getInfo.getFiles(Dir, Mask);
        if(selectiveLine.length() == 0)
        {
            selectiveLine = "1,0";
        }
        String p[] = selectiveLine.split(",");
        ProcLine = Integer.parseInt(p[0]);
        escapeLine = Integer.parseInt(p[1]);
        Date date = new Date();
        for(int i = 0; i < files.length; i++)
        {
            fileName = files[i].toString();
            System.out.println((new StringBuilder("Loading File ")).append(fileName).append(" started at \t").append(date.toString()).toString());
            RowNo = 0;
            getStatus.DebugFileStoreExcept("\n\n\n", sessNo);
            getStatus.DebugFileStoreExcept((new StringBuilder(String.valueOf(fileName))).append(" started at \t").append(date.toString()).toString(), sessNo);
            getStatus.DebugFileStoreExcept("\n\n", sessNo);
            map = new HashMap();
            ps.clearBatch();
            pserror.clearBatch();
            commitNo = 0;
            ProcessingRowNo = 0;
            if(Footer > 0)
            {
                footerData = new ArrayList();
                revFL = new ReversedLinesFileReader(files[i]);
                while((Line = revFL.readLine()) != null) 
                {
                    RowNo++;
                    footerData.add(Line);
                    if(RowNo >= Footer)
                    {
                        break;
                    }
                }
                revFL.close();
            }
            RowNo = 0;
            LineNo = 0;
            for(it = FileUtils.lineIterator(new File(fileName), CharacterSet); it.hasNext();)
            {
                RowNo++;
                Line = it.nextLine();
                if(RowNo > Heading && (ignoreLine.equals("") || Line.indexOf(ignoreLine) < 0))
                {
                    Datacol = Line.split(delimiter, -1);
                    if(LineNo <= ProcLine && !footerData.contains(Line))
                    {
                        LineNo++;
                        String c[] = DataColumns.split("Datacol");
                        int colno = 0;
                        String as1[];
                        int j1 = (as1 = c).length;
                        for(int i1 = 0; i1 < j1; i1++)
                        {
                            String string = as1[i1];
                            if(string.length() > 0)
                            {
                                int value = Integer.parseInt(string.substring(1, string.indexOf("]")));
                                data = HandleData.TrimData(Datacol[value], textDelim, Remove_Special_Characters);
                                ps.setObject(colno + 1, data);
                                build.append((new StringBuilder(String.valueOf(data))).append(";").toString());
                                colno++;
                            }
                        }

                        ProcessingRowNo++;
                        ps.addBatch();
                        keyNo++;
                        if(build.length() > 0)
                        {
                            map.put(Integer.valueOf(keyNo), (new StringBuilder(String.valueOf(RowNo))).append(";").append(build.substring(0, build.length() - 1)).toString());
                        }
                        build = new StringBuilder();
                        commitNo++;
                        if(commitNo == commitsize)
                        {
                            try
                            {
                                ps.executeBatch();
                                conn.commit();
                            }
                            catch(BatchUpdateException e)
                            {
                                DebugFileErrors.DebugErrors(ps, pserror, map, e.getUpdateCounts().length, fileName, tableName, sessNo);
                            }
                            commitNo = 0;
                            keyNo = 0;
                            map = new HashMap();
                        }
                    }
                    if(LineNo >= ProcLine)
                    {
                        if(LineNo == ProcLine + escapeLine)
                        {
                            LineNo = 0;
                        } else
                        {
                            LineNo++;
                        }
                    }
                }
            }

            try
            {
                ps.executeBatch();
                conn.commit();
            }
            catch(BatchUpdateException e)
            {
                DebugFileErrors.DebugErrors(ps, pserror, map, e.getUpdateCounts().length, fileName, tableName, sessNo);
            }
            commitNo = 0;
            build = new StringBuilder();
            map = new HashMap();
            keyNo = 0;
            if(Move_Copy_Files.length() > 0)
            {
                if(Move_Copy_Files.toUpperCase().equals("COPY"))
                {
                    FileMove.CopyFile(Dir.replace("/", "\\"), Move_Copy_Files_Dir, fileName);
                } else
                if(Move_Copy_Files.toUpperCase().equals("MOVE"))
                {
                    FileMove.MoveFile(Dir.replace("/", "\\"), Move_Copy_Files_Dir, fileName);
                }
            }
            date = new Date();
            System.out.println((new StringBuilder("Loading File ")).append(fileName).append(" Completed at ").append(date.toString()).toString());
            getStatus.DebugFileStoreExcept((new StringBuilder(String.valueOf(fileName))).append(" Completed at ").append(date.toString()).toString(), sessNo);
            getStatus.DebugFileStoreExcept((new StringBuilder("\nRows Inserted From File  - ")).append(ProcessingRowNo).toString(), sessNo);
            getStatus.DebugFileStoreExcept("\n\n", sessNo);
        }

        ps.close();
    }

}
