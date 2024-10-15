package com.garinternal.common.excel.xlsx;

/*
File Name:                      StreamingReader.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains utility methods to read .xlsx files.
Streaming Excel workbook implementation. Most advanced features of POI are not supported.
Use this only if your application can handle iterating through an entire workbook, row by row.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.excel.xlsx.impl.StreamingWorkbook;
import com.garinternal.common.excel.xlsx.impl.StreamingWorkbookReader;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class StreamingReader implements Iterable<Row>, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(StreamingReader.class);

    private File                          tmp;
    private final StreamingWorkbookReader workbook;

    /**
     * Constructor
     * 
     * @param workbook {@link StreamingWorkbookReader}
     */
    public StreamingReader(StreamingWorkbookReader workbook) {
        this.workbook = workbook;
    }

    /**
     * Returns a new streaming iterator to loop through rows. This iterator is not
     * guaranteed to have all rows in memory, and any particular iteration may
     * trigger a load from disk to read in new data.
     *
     * @return the streaming iterator
     * @deprecated StreamingReader is equivalent to the POI Workbook object rather
     *             than the Sheet object. This method will be removed in a future release.
     */
    @Deprecated
    @Override
    public Iterator<Row> iterator() {
        return this.workbook.first().iterator();
    }

    /**
     * Closes the streaming resource, attempting to clean up any temporary files created.
     *
     * @throws com.garinternal.common.excel.xlsx.exceptions.CloseException if there is an issue closing the stream
     */
    @Override
    public void close() throws IOException {

        try {
            this.workbook.close();
        } finally {

            if (this.tmp != null) {

                if (log.isDebugEnabled()) {
                    log.debug("Deleting tmp file [" + this.tmp.getAbsolutePath() + "]");
                }

                this.tmp.delete();
            }

        }

    }

    /**
     * Return Builder instance
     * 
     * @return Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder Class
     */
    public static class Builder {
        private int    rowCacheSize      = 10;
        private int    bufferSize        = 1024;
        private int    sstCacheSizeBytes = -1;
        private String password;

        public int getRowCacheSize() {
            return this.rowCacheSize;
        }

        public int getBufferSize() {
            return this.bufferSize;
        }

        /**
         * @return The password to use to unlock this workbook
         */
        public String getPassword() {
            return this.password;
        }

        /**
         * @return The size of the shared string table cache. If less than 0, no
         *         cache will be used and the entire table will be loaded into memory.
         */
        public int getSstCacheSizeBytes() {
            return this.sstCacheSizeBytes;
        }

        /**
         * The number of rows to keep in memory at any given point.
         * <p>
         * Defaults to 10
         * </p>
         *
         * @param rowCacheSize number of rows
         * @return reference to current {@code Builder}
         */
        public Builder rowCacheSize(int rowCacheSize) {
            this.rowCacheSize = rowCacheSize;
            return this;
        }

        /**
         * The number of bytes to read into memory from the input
         * resource.
         * <p>
         * Defaults to 1024
         * </p>
         *
         * @param bufferSize buffer size in bytes
         * @return reference to current {@code Builder}
         */
        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        /**
         * For password protected files specify password to open file.
         * If the password is incorrect a {@code ReadException} is thrown on
         * {@code read}.
         * <p>
         * NULL indicates that no password should be used, this is the
         * default value.
         * </p>
         *
         * @param password to use when opening file
         * @return reference to current {@code Builder}
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * <h1>!!! This option is experimental !!!</h1>
         *
         * Set the size of the Shared Strings Table cache. This option exists to accommodate
         * extremely large workbooks with millions of unique strings. Normally the SST is entirely
         * loaded into memory, but with large workbooks with high cardinality (i.e., very few
         * duplicate values) the SST may not fit entirely into memory.
         * <p>
         * By default, the entire SST *will* be loaded into memory. Setting a value greater than
         * 0 for this option will only cache up to this many entries in memory. <strong>However</strong>,
         * enabling this option at all will have some noticeable performance degredation as you are
         * trading memory for disk space.
         *
         * @param sstCacheSizeBytes size of SST cache
         * @return reference to current {@code Builder}
         */
        public Builder sstCacheSizeBytes(int sstCacheSizeBytes) {
            this.sstCacheSizeBytes = sstCacheSizeBytes;
            return this;
        }

        /**
         * Reads a given {@code InputStream} and returns a new
         * instance of {@code Workbook}. Due to Apache POI
         * limitations, a temporary file must be written in order
         * to create a streaming iterator. This process will use
         * the same buffer size as specified in {@link #bufferSize(int)}.
         *
         * @param is input stream to read in
         * @return A {@link Workbook} that can be read from
         * @throws com.garinternal.common.excel.xlsx.exceptions.ReadException if there is an issue reading the stream
         */
        public Workbook open(InputStream is) {
            StreamingWorkbookReader workbook = new StreamingWorkbookReader(this);
            workbook.init(is);
            return new StreamingWorkbook(workbook);
        }

        /**
         * Reads a given {@code File} and returns a new instance
         * of {@code Workbook}.
         *
         * @param file file to read in
         * @return built streaming reader instance
         * @throws com.garinternal.common.excel.xlsx.exceptions.OpenException if there is an issue opening the file
         * @throws com.garinternal.common.excel.xlsx.exceptions.ReadException if there is an issue reading the file
         */
        public Workbook open(File file) {
            StreamingWorkbookReader workbook = new StreamingWorkbookReader(this);
            workbook.init(file);
            return new StreamingWorkbook(workbook);
        }
    }

}
