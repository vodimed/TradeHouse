package com.expertek.tradehouse.documents;

//ROOM: import com.expertek.tradehouse.documents.room.*;
import com.expertek.tradehouse.documents.sqlite.*;

public interface DBDocuments {
    Documents documents();
    Lines lines();
    Marklines marklines();
}
