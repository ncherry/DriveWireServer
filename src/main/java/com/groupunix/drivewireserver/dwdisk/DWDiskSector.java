package com.groupunix.drivewireserver.dwdisk;

import org.apache.commons.vfs2.RandomAccessContent;
import org.apache.commons.vfs2.util.RandomAccessMode;

import java.io.IOException;


public class DWDiskSector {
    private final int LSN;
    private byte[] data;
    //private byte[] dirtydata;
    private boolean dirty = false;
    private final int sectorsize;
    private final DWDisk disk;
    private final boolean direct;


    public DWDiskSector(DWDisk disk, int lsn, int sectorsize, boolean direct) {
        this.LSN = lsn;
        this.sectorsize = sectorsize;
        this.direct = direct;
        this.disk = disk;

        if (!direct) {
            this.data = new byte[sectorsize];
        }


    }

    public int getLSN() {
        return this.LSN;
    }

    public void setData(byte[] newdata) {
        if (this.data == null) {
            this.data = new byte[newdata.length];
        }

        this.dirty = true;
        System.arraycopy(newdata, 0, this.data, 0, this.sectorsize);

    }

    public synchronized void setData(byte[] newdata, boolean dirty) {
        this.dirty = dirty;

        if (this.data == null) {
            this.data = new byte[newdata.length];
        }

        System.arraycopy(newdata, 0, this.data, 0, this.sectorsize);
    }


    public synchronized byte[] getData() throws IOException {
        if (this.data != null) {
            return this.data;
        }
        else if (this.direct) {
            return this.getFileSector();
        }
        else {
            return new byte[this.sectorsize];
        }
    }


    public synchronized void makeClean() {
        if (this.dirty) {
            if (this.direct) {
                this.data = null;
            }
            this.dirty = false;
        }
    }


    private byte[] getFileSector() throws IOException {

        RandomAccessContent raf = this.disk.getFileObject().getContent().getRandomAccessContent(RandomAccessMode.READ);
        long pos = this.LSN * this.sectorsize;
        raf.seek(pos);
        byte[] buf = new byte[this.sectorsize];
        raf.readFully(buf);
        raf.close();
        return buf;
    }


    public synchronized boolean isDirty() {
        return dirty;
    }

    public void setDataByte(int i, byte b) throws IOException {
        if (this.data == null) {
            this.data = this.getFileSector();
        }

        this.data[i] = b;
        this.dirty = true;
    }


    public void makeDirty() {
        this.dirty = true;

    }

}
