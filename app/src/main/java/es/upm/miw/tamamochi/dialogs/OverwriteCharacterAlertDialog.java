package es.upm.miw.tamamochi.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import es.upm.miw.tamamochi.MainActivity;
import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.activities.CharacterCreationActivity;

public class OverwriteCharacterAlertDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) getActivity();

        assert main != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle(R.string.txtOverwriteCharacterAlertDialogTitle)
                .setMessage(R.string.txtOverwriteCharacterAlertDialogQuestion)
                .setPositiveButton(
                        getString(R.string.txtOverwriteCharacterAlertDialogAffirmative),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(main, CharacterCreationActivity.class);
                                startActivity(intent);
                            }
                        }
                )
                .setNegativeButton(
                        getString(R.string.txtOverwriteCharacterAlertDialogNegative),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                );

        return builder.create();
    }
}
