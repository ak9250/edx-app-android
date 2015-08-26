package org.edx.mobile.task;

import android.content.Context;
import android.support.annotation.NonNull;

import org.edx.mobile.discussion.DiscussionThread;

public abstract class GetThreadTask extends
        Task<DiscussionThread> {

    @NonNull
    final String threadId;

    public GetThreadTask(@NonNull Context context, @NonNull String threadId) {
        super(context);
        this.threadId = threadId;
    }


    public DiscussionThread call() throws Exception {
        try {
            return environment.getDiscussionAPI().getThread(threadId);

        } catch (Exception ex) {
            handle(ex);
            logger.error(ex, true);
        }
        return null;
    }
}
