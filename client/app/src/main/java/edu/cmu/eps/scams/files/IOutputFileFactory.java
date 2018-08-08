package edu.cmu.eps.scams.files;

import java.io.File;

/**
 * Created by jeremy on 3/5/2018.
 * This interface defines the construction of new file objects.
 */

public interface IOutputFileFactory {

    public File build();
}
