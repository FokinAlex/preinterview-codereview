package export;

// Project imports:
//      ProcException
//      IDocumentProc
//      ParameterSet
//      DocStates
//      Document
//      DocProcessResultList
//      DocumentProcessResult
//      DictionaryService
//      TaskToken
//      Service
//      PathFactory

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

public class CsvExportProc implements IDocumentProc {

    // Procedure params:
    private static final String PARAM_NAME_TARGET_DIR        = "targetDir";
    private static final String PARAM_NAME_EMPTY_FIELD_VALUE = "emptyFieldValue";

    // File name mask PREFIX_YYYYMMDD_HHMMSS.csv
    private static final String FILE_NAME_PREFIX      = "PREFIX_";
    private static final String FILE_NAME_DATE_FORMAT = "yyyyMMdd_HHmmss";
    private static final String FILE_NAME_EXTENSION   = ".csv";

    // Available states:
    private static final Set<String> FINAL_STATES = ImmutableSet.of(
            DocStates.REFUSEDBYBANK,
            DocStates.REQUISITEERROR,
            DocStates.INVALIDEDS,
            DocStates.IMPLEMENTED
    );

    // Messages:
    private static final String PROCEDURE_MESSAGE_BEGINNING = "Procedure start message stub";
    private static final String PROCEDURE_MESSAGE_DIRECTORY_UNSPECIFIED = "Directory unspecified message stub";
    private static final String PROCEDURE_MESSAGE_DIRECTORY_EXISTENCE_EXCEPTION = "Directory existence message stub: %s";
    private static final String PROCEDURE_MESSAGE_DOCUMENT_EXPORT_EXCEPTION = "Document export exception message stub (docid = %s)";
    private static final String PROCEDURE_MESSAGE_EXPORT_EXCEPTION = "Procedure exception message stub";
    private static final String PROCEDURE_MESSAGE_ENDING = "Procedure end message stub";

    @Service("dictionaryService")
    private DictionaryService dictionaryService;

    private Logger logger;

    @Override
    public DocProcessResultList execute(TaskToken taskToken,
                                        ParameterSet params,
                                        Collection<Document> documents) throws ProcException {

        logger.info(PROCEDURE_MESSAGE_BEGINNING);

        // Target directory param check
        String targetDir = params.get(PARAM_NAME_TARGET_DIR);
        if (!StringUtils.hasLength(targetDir)) {
            logger.error(PROCEDURE_MESSAGE_DIRECTORY_UNSPECIFIED);
            throw new ProcException(PROCEDURE_MESSAGE_DIRECTORY_UNSPECIFIED);
        }

        // Empty fields param
        String emptyFieldValue = params.get(PARAM_NAME_EMPTY_FIELD_VALUE);
        if (StringUtils.hasLength(emptyFieldValue)) {
            StructuralDepositRqToCSVConvertUtils.setEmptyFieldValue(emptyFieldValue);
        }

        // Try to create target directory
        File directory = PathFactory.getFileInstance(targetDir);
        if (!directory.exists() && !directory.mkdirs() || !directory.isDirectory()) {
            logger.error(PROCEDURE_MESSAGE_DIRECTORY_EXISTENCE_EXCEPTION);
            throw new ProcException(String.format(PROCEDURE_MESSAGE_DIRECTORY_EXISTENCE_EXCEPTION, directory));
        }

        // Procedure body
        DocProcessResultList processResults = new DocProcessResultList();
        try {
            // File initialization ${directory}/PREFIX_YYYYMMDD_HHMMSS.csv
            File target = PathFactory.getFileInstance(directory, getTargetFileName());

            // Metadata writing
            FileOutputStream outputStream = PathFactory.getFileOutputStreamInstance(target);
            outputStream.write(Converter.META_LINE.getBytes(Charsets.UTF_8));
            outputStream.close();

            // Data writing
            String csvLine;
            Date today = new Date();
            for(Document document : documents) {
                if (inFinalState(document) && DateUtils.isSameDay(document.getLastModifyDate(), today)) {
                    DepositType depositType = DepositType.getDepositType(document, dictionaryService);
                    csvLine = Converter.depositToCSVLine(document, depositType);
                    try {
                        outputStream = PathFactory.getFileOutputStreamInstance(target, true);
                        outputStream.write(csvLine.getBytes(Charsets.UTF_8));
                        outputStream.close();
                    } catch (IOException e) {
                        logger.error(String.format(PROCEDURE_MESSAGE_DOCUMENT_EXPORT_EXCEPTION, document.getDocId()), e);
                        processResults.add(new DocumentProcessResult(e, document));
                    } finally {
                        IOUtils.closeQuietly(outputStream);
                    }
                }
            }
        }
        catch(IOException e) {
            logger.error(PROCEDURE_MESSAGE_EXPORT_EXCEPTION, e);
            throw new ProcException(PROCEDURE_MESSAGE_EXPORT_EXCEPTION);
        }

        logger.info(PROCEDURE_MESSAGE_ENDING);
        return processResults;
    }

    private boolean inFinalState(Document document) {
        return FINAL_STATES.contains(document.getBankDocState().getSystemName());
    }

    private static String getTargetFileName() {
        return FILE_NAME_PREFIX
                + FastDateFormat.getInstance(FILE_NAME_DATE_FORMAT).format(Calendar.getInstance())
                + FILE_NAME_EXTENSION;
    }

    public void setLogger(Logger logger) {
        this.logger = logger != null ? logger : Logger.getLogger(CsvExportProc.class);
    }
}
