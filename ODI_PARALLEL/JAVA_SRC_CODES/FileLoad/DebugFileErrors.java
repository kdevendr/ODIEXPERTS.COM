package FileLoad;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class DebugFileErrors
{

    private static String errMsg;

    public DebugFileErrors()
    {
    }

    public static void DebugErrors(PreparedStatement ps, PreparedStatement pserror, HashMap map, int updateNo, String fileName, String tableName, int sessNo)
    {
        try
        {
            for(int i = updateNo + 1; i < map.size() + 1; i++)
            {
                Object columns[] = map.get(Integer.valueOf(i)).toString().split(";", -1);
                for(int j = 1; j < columns.length; j++)
                {
                    ps.setObject(j, columns[j]);
                }

                try
                {
                    ps.executeUpdate();
                }
                catch(SQLException e1)
                {
                    pserror.setString(1, fileName);
                    pserror.setString(2, tableName);
                    pserror.setString(3, columns[0].toString());
                    for(Iterator iterator = e1.iterator(); iterator.hasNext(); pserror.setString(4, errMsg))
                    {
                        Throwable throwable = (Throwable)iterator.next();
                        errMsg = throwable.toString().substring(throwable.toString().lastIndexOf("Exception:") + 10, throwable.toString().length()).trim();
                    }

                    Date utilDate = new Date();
                    pserror.setDate(5, new java.sql.Date(utilDate.getTime()));
                    pserror.setInt(6, sessNo);
                    for(int j = 1; j < columns.length; j++)
                    {
                        if(columns[j].toString().length() >= 255)
                        {
                            pserror.setString(j + 6, columns[j].toString().substring(0, 254));
                        } else
                        {
                            pserror.setString(j + 6, columns[j].toString());
                        }
                    }

                    pserror.executeUpdate();
                }
            }

        }
        catch(SQLException sqlexception)
        {
            sqlexception.printStackTrace();
        }
        map = new HashMap();
    }
}
