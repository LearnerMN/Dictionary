package dic.learnermn.com.dictionary;

import android.os.AsyncTask;

import java.util.List;


public class DataLoader extends AsyncTask<String, Void, List<Bean>> {
	
	private DictionaryDB dictionaryDB;
	private WordListAdapter adapter;
	
	public DataLoader(DictionaryDB dictionaryDB, WordListAdapter adapter) {
		this.dictionaryDB = dictionaryDB;
		this.adapter = adapter;
	}

	@Override
	protected List<Bean> doInBackground(String... params) {
		return dictionaryDB.getWords(params[0]);
	}
	
	@Override
	protected void onPostExecute(List<Bean> result) {
		adapter.updateEntries(result);
	}
}
