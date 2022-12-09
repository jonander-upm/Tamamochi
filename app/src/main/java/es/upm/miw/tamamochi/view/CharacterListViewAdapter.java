package es.upm.miw.tamamochi.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.domain.model.Character;
import es.upm.miw.tamamochi.domain.model.CharacterAge;

import java.util.List;

public class CharacterListViewAdapter extends BaseAdapter {
    Context context;
    List<Character> characters;
    LayoutInflater inflater;

    static class CharacterViewHolder {
        ImageView ivCharacter;
        TextView tvCharacterName;
        TextView tvCharacterType;
        TextView tvCharacterBirthDate;
    }

    public CharacterListViewAdapter(Context applicationContext, List<Character>  characters) {
        this.context = applicationContext;
        this.characters = characters;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return characters != null ?
                characters.size()
                : 0;
    }

    @Override
    public Object getItem(int position) {
        return characters != null ?
                characters.get(position)
                : null;
    }

    @Override
    public long getItemId(int position) {
        return characters != null ?
                characters.get(position).getCharacterId().hashCode()
                : 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        final CharacterViewHolder viewHolder;
        if(view == null) {
            viewHolder = new CharacterViewHolder();
            view = inflater.inflate(R.layout.character_list_item, parent, false);
            viewHolder.ivCharacter = view.findViewById(R.id.ivCharacterImg);
            viewHolder.tvCharacterName = view.findViewById(R.id.tvCharacterName);
            viewHolder.tvCharacterType = view.findViewById(R.id.tvCharacterType);
            viewHolder.tvCharacterBirthDate = view.findViewById(R.id.tvCharacterBirthDate);
            view.setTag(viewHolder);
        } else {
            viewHolder = (CharacterViewHolder) view.getTag();
        }
        if(characters != null) {
            Character character = characters.get(i);
            CharacterAge characterAge = character.getAlive()
                ? CharacterAge.getCharacterAge(character.getCharacterBirthDate())
                : CharacterAge.DEAD;
            viewHolder.ivCharacter.setImageResource(character.getCharacterType().getDrawableIdByAge(characterAge));
            viewHolder.tvCharacterName.setText(character.getCharacterName());
            viewHolder.tvCharacterType.setText(character.getCharacterType().toString());
            viewHolder.tvCharacterBirthDate.setText(character.getCharacterBirthDate().toString());
        }
        return view;
    }
}
