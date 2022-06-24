//package com.naeggeodo.logconfig;
//
//import com.p6spy.engine.logging.Category;
//import com.p6spy.engine.spy.P6SpyOptions;
//import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
//import org.hibernate.engine.jdbc.internal.FormatStyle;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.PostConstruct;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class P6spyPrettySqlFormatter implements MessageFormattingStrategy {
//    //https://backtony.github.io/spring/2021-08-13-spring-log-1/
//    @Override
//    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
//        sql = formatSql(category, sql);
//        Date currentDate = new Date();
//
//        SimpleDateFormat format1 = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
//
//        //return now + "|" + elapsed + "ms|" + category + "|connection " + connectionId + "|" + P6Util.singleLine(prepared) + sql;
//        return format1.format(currentDate) + " | "+ "OperationTime : "+ elapsed + "ms" + sql;
//    }
//
//    private String formatSql(String category,String sql) {
//        if(sql ==null || sql.trim().equals("")) return sql;
//
//        // Only format Statement, distinguish DDL And DML
//        if (Category.STATEMENT.getName().equals(category)) {
//            String tmpsql = sql.trim().toLowerCase(Locale.ROOT);
//            if(tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
//                sql = FormatStyle.DDL.getFormatter().format(sql);
//            }else {
//                sql = FormatStyle.BASIC.getFormatter().format(sql);
//            }
//            sql = "|\nHeFormatSql(P6Spy sql,Hibernate format):"+ sql;
//        }
//
//        return sql;
//    }
//}
//@Configuration
//class P6SpyConfig{
//    @PostConstruct
//    public void setLogMessageFormat(){
//        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spyPrettySqlFormatter.class.getName());
//    }
//}